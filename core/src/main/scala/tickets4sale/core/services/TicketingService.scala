package tickets4sale.core.services

import cats.Functor
import tickets4sale.core.db.models.{Show => DbShow}
import tickets4sale.core.db.repositories.Repository
import tickets4sale.core.model.report.{Inventory, Report}
import tickets4sale.core.syntax.DateSyntax._
import cats.implicits._
import tickets4sale.core.model.Show
import tickets4sale.core.model.places.{BigHall, Place, SmallHall}

/**
  * Encapsulates all the logic of the business model
  * @param repo the database repository
  * @tparam F
  */
class TicketingService[F[_]:Functor](repo: Repository[F]) {

  /**
    * Get the report of all shows at the given date(`showDate`)
    * @param queryDate the query date
    * @param showDate the show date
    * @return
    */
  def getReport(queryDate: String, showDate: String): F[Either[Throwable,Report]] = {

    val showDateinSql = showDate.toSqlDate()

    val dbShows: F[Either[Throwable,Seq[DbShow]]] = repo.findShowsAtSpecificDate(showDateinSql)

    dbShows.map { excOrShows =>
      excOrShows.map { shows =>
        val enrichedShows = shows map { show =>

          val dayNumber = show.openingDay.daysBetween(showDateinSql)+1
          Show(
            title = show.title,
            genre = show.genre,
            place = findPlaceByDay(dayNumber),
            dayNumber = dayNumber,
            date = showDateinSql.toLocalDate)
        }

        val reports = enrichedShows.map(_.getReport(queryDate.toLocalDate()))
        val reportsByGenre = reports.toList.groupBy(_.genre)
        Report( Inventory.fromMap(reportsByGenre) )
      }
    }
  }

  private def findPlaceByDay(dayNumber: Long): Place = {
    if (dayNumber >= 1 && dayNumber <= 60)
      BigHall
    else
      SmallHall
  }

}
