package tickets4sale.cli

import cats.{Applicative, Functor, Id, Monad}
import cats.effect.IO
import tickets4sale.core.util.Validation._
import tickets4sale.core.syntax.ValidatedSyntax._
import cats.implicits._
import tickets4sale.core.db.DbInitializer
import tickets4sale.core.db.repositories.{H2Repository, InMemoryRepository, Repository}
import tickets4sale.core.model.report.Report
import tickets4sale.core.services.TicketingService
import tickets4sale.core.util.InventoryCsvReader
import io.circe.syntax._
import io.circe.generic.auto._
import tickets4sale.cli.json.Encoders._
import tickets4sale.core.Builder
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object Main {

  implicit val inMemoryRepoBuilder: Builder[InMemoryRepository] = () => new InMemoryRepository
  implicit val h2RepoBuilder: Builder[H2Repository] = () => {
    val db = Database.forConfig("h2-db")
    val repository = new H2Repository(db)
    Await.result(repository.createTables(), Duration.Inf)
    repository
  }

  def printMessage(message: String): IO[Unit] = IO {print(message)}
  def printLogo(): IO[Unit] = printMessage("\n\n  _______      __        __       __ __             __   \n /_  __(_)____/ /_____  / /______/ // / _________ _/ /__ \n  / / / / ___/ //_/ _ \\/ __/ ___/ // /_/ ___/ __ `/ / _ \\\n / / / / /__/ ,< /  __/ /_(__  )__  __(__  ) /_/ / /  __/\n/_/ /_/\\___/_/|_|\\___/\\__/____/  /_/ /____/\\__,_/_/\\___/ \n                                                         \n\n")
  def printGreeting(): IO[Unit] = printMessage("Welcome to tickets4sale CLI\n")
  def getUserInput(): IO[String] = IO {
    StdIn.readLine()
  }

  /**
    * Get the filename from the user
    * If filename has not a `csv` extension or does not exist it repeats the process
    * @return the filename
    */
  def filenameLoop(): IO[String] = for {
    _ <- printMessage("Enter filename:")
    filename <- getUserInput()
    isValid <- hasValidExtension(filename, Set(".csv")).andThen(fileExists).liftToIO
    filename <- if (isValid) IO.pure(filename) else filenameLoop()
  } yield filename

  /**
    * Get date in the format of `yyyy-mm-dd` from user
    * If the date is invalid it asks the user to give another date
    * @param message the message that will be shown to the user
    * @return the date
    */
  def dateLoop(message: String): IO[String] = for {
    _ <- printMessage(message)
    date <- getUserInput()
    isValid <- isValidDateFormat(date).liftToIO
    date <- if (isValid) IO.pure(date) else dateLoop(message)
  } yield date

  def createRepository[F[_]:Applicative, R <: Repository[F]](csvFilename: String)
                                                        (implicit repoBuilder: Builder[R]): IO[F[Either[Throwable, R]]] = {
    val repository = repoBuilder.build()

    InventoryCsvReader.read(csvFilename) map { fileContents =>
      // add rows to the database
      DbInitializer.initialize(fileContents,repository)
    } map { _.map {
        case Right(_) => Right(repository)
        case Left(exc) => Left(exc)
      }
    }
  }

  def createService[F[_]:Functor, R <: Repository[F]](repository: F[Either[Throwable, R]]): F[Either[Throwable,TicketingService[F]]] = {
    repository.map { excOrRepo =>
      for {
        repo <- excOrRepo
      } yield new TicketingService(repo)
    }
  }

  def getReport[F[_]:Monad](service: F[Either[Throwable,TicketingService[F]]],
                            queryDate: String,
                            showDate: String): F[Either[Throwable,Report]] = {
    service.flatMap {
      case Right(service) => service.getReport(queryDate,showDate)
      case Left(exc) => implicitly[Monad[F]].pure(Left(exc))
    }
  }

  def getJson[F[_]:Functor](report: F[Either[Throwable, Report]]): F[Either[Throwable, String]] = {
    report.map { excOrReport =>
      for {
        report <- excOrReport
      } yield report.asJson.toString
    }
  }

  def cli[F[_]: Monad, R <: Repository[F]](implicit repoBuilder:Builder[R]): IO[F[Either[Throwable,String]]] = for {
    _ <- printLogo()
    _ <- printGreeting()

    filename <- filenameLoop()
    queryDate <- dateLoop("Enter query date:")
    showDate <- dateLoop("Enter show date:")

    repository <- createRepository[F,R](filename)
    service = createService[F,R](repository)
    report = getReport[F](service, queryDate, showDate)
  } yield getJson[F](report)

  def main(args: Array[String]): Unit = {

    cli[Id , InMemoryRepository].unsafeRunSync() match {
      case Left(exc) =>
        println("Error:")
        println(exc.getMessage)
      case Right(json) =>
        println(json)
    }

    //=====================================
    // If you want to replace F with Future then comment above lines
    // and uncomment bellow lines
    //=====================================

//    Await.result(cli[Future , H2Repository].unsafeRunSync(), Duration.Inf) match {
//      case Left(exc) =>
//        println("Error:")
//        println(exc.getMessage)
//      case Right(json) =>
//        println(json)
//    }

  }


}
