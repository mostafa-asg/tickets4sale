package tickets4sale.core.model.timeline

import java.time.LocalDate

import tickets4sale.core.model.Show
import tickets4sale.core.Constants._
import cats.implicits._

sealed trait ShowTimeline

object SaleHasNotStarted extends ShowTimeline
object AllTicketsSoldOut extends ShowTimeline
object ShowHasEnded extends ShowTimeline

object OpenForSale extends ShowTimeline {

  private case class Day(date: LocalDate, soldTickets: Int)

  case class Report(
    ticketsLeft: Int,
    ticketsAvailable: Int
  )

  def getSaleReport(show: Show, queryDate: LocalDate): Report = {

    def getNumberOfSoldTickets(date: LocalDate): Int = {
      if (date.isBefore(queryDate))
        show.dailySellLimit
      else
        show.soldTickets.get(date) match {
          case Some(tickets) => tickets.foldMap(_.numberOfTickets)
          case None => 0
        }
    }

    val allDays = List.fill(SaleDurationInDays)(show.saleStartDate)
                   .zipWithIndex.map {
                      case (date, dayIndex) =>
                        val currentDay = date.plusDays(dayIndex)
                        Day(currentDay, getNumberOfSoldTickets(currentDay))
                   }

    val todaySell = getNumberOfSoldTickets(queryDate)

    Report(
      ticketsAvailable = show.dailySellLimit - todaySell,
      ticketsLeft = show.totalTickets - allDays.foldMap(_.soldTickets)
    )
  }

}
