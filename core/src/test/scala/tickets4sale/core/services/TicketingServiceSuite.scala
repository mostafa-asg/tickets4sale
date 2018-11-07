package tickets4sale.core.services

import org.scalatest.{FunSuite, Matchers}
import tickets4sale.core.db.DbInitializer
import tickets4sale.core.db.repositories.{H2Repository, InMemoryRepository}
import tickets4sale.core.model.report.{Inventory, Report, ShowReport}
import tickets4sale.core.Constants._
import slick.jdbc.JdbcBackend.Database
import cats.implicits._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Right

class TicketingServiceSuite extends FunSuite with Matchers {

  test("InMemory Repository") {
    val repository = new InMemoryRepository
    val shows = List(
      ("1984","2017-10-14","DRAMA"),
      ("39 STEPS, THE ","2017-11-10","COMEDY")
    )

    DbInitializer.initialize(shows, repository)
    val service = new TicketingService(repository)

    // show is on day 2 (2017-10-15)
    // query date is on day 11
    val report = service.getReport(queryDate = "2017-10-02", showDate = "2017-10-15")
    val expectedResult = Right(Report(List(Inventory("DRAMA",List(
      ShowReport("1984", "DRAMA", DramaPrice, 200-(11*10), 10, "Open for sale"))))))
    report shouldBe expectedResult
  }

  test("H2Repository") {

    val db = Database.forConfig("h2-db")
    val repository = new H2Repository(db)
    Await.result(repository.createTables(), Duration.Inf) match {
      case Left(exc) => fail(exc)
      case _ =>
    }

    val shows = List(
      ("1984","2017-10-14","DRAMA"),
      ("39 STEPS, THE ","2017-11-10","COMEDY")
    )
    Await.result(DbInitializer.initialize(shows, repository), Duration.Inf) match {
      case Left(exc) => fail(exc)
      case _ =>
    }
    val service = new TicketingService(repository)

    // show is on day 100 (2018-02-17)
    // query date is on day 20 (last day to buy tickets)
    val report = Await.result(service.getReport("2018-02-12", "2018-02-17"), Duration.Inf)
    val expectedResult = Right(Report(List(Inventory("COMEDY",List(
      ShowReport("39 STEPS, THE ", "COMEDY", ComedyPrice - (20 * ComedyPrice / 100), 5, 5, "Open for sale",false))))))
    report shouldBe expectedResult
  }


}
