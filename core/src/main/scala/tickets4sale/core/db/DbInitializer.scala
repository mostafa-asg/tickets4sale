package tickets4sale.core.db

import cats.Applicative
import cats.effect.IO
import tickets4sale.core.db.models.Show
import tickets4sale.core.db.repositories.Repository
import tickets4sale.core.Constants._
import tickets4sale.core.syntax.DateSyntax._

import cats.implicits._

/**
  * Fills the database tables using the given `Repository`
  */
object DbInitializer {

  type ErrorOr[A] = Either[Throwable,A]

  /**
    * Fill the tables
    * @param shows list of shows in the format of 'title','opening-date','genre'
    * @param repo the repository
    * @tparam F
    * @return Either exception or `unit` if all the operation was successful
    */
  def initialize[F[_] : Applicative](shows: List[(String,String,String)], repo: Repository[F]): F[ErrorOr[Unit]] = {

    /**
      * Converts the input to database's show
      */
    val enrichedShows = shows.map { show =>
      val (title, date, genre) = show
      val openingDate = date.toSqlDate()
      val lastDay = openingDate.plusDays(ShowsDurationInDay)

      Show(
        title = title,
        genre = genre,
        openingDay = openingDate,
        lastDay = lastDay,
        saleStartDay = openingDate.minusDays(OpenForSaleInDay),
        saleEndDay = lastDay.minusDays(CloseSaleInDay)
      )
    }

    val listOfF: List[F[ErrorOr[Int]]] = enrichedShows.map( repo.insertShow )

    val f_andList: F[List[ErrorOr[Int]]] = listOfF.sequence

    val f_andEither: F[ErrorOr[List[Int]]] = f_andList.map( _.sequence )

    f_andEither map {
      case Right(_) => Right(())
      case Left(exc) => Left(exc)
    }

  }

}
