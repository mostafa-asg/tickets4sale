package tickets4sale.core.model

import java.time.LocalDate

import tickets4sale.core.model.places._
import tickets4sale.core.Constants._
import tickets4sale.core.syntax.DateSyntax._
import tickets4sale.core.model.report.ShowReport
import tickets4sale.core.model.timeline._

/**
  * Represents `show`
  * @param title show's title
  * @param genre show's genre
  * @param place show's place
  * @param dayNumber the show's day number
  * @param date the date that this show will be run
  * @param soldTickets the information of sold tickets
  */
case class Show(title: String,
                genre: String,
                place: Place,
                dayNumber: Long,
                date: LocalDate,
                soldTickets: Map[LocalDate,List[TicketInfo]] = Map.empty) extends ShowMetadata {

  private val discountPercent: Double = if (dayNumber>=80 && dayNumber<=100) Discount else 0

  /**
    * Price with applying discount
    */
  val price: Double = genre.toLowerCase match {
    case "musical" => MusicalPrice - (discountPercent * MusicalPrice / 100)
    case "comedy" => ComedyPrice - (discountPercent * ComedyPrice / 100)
    case "drama" => DramaPrice - (discountPercent * DramaPrice / 100)
    case _ => 0
  }

  /**
    * How many tickets we are going to sell for this show?
    */
  val totalTickets: Int = place.capacity

  /**
    * When users can buy tickets
    */
  val saleStartDate: LocalDate = date.minusDays(OpenForSaleInDay)

  /**
    * When users cannot buy any tickets anymore
    * In this date we do not sell any tickets or
    * all the tickets has been sold out
    */
  val saleCloseDate: LocalDate = date.minusDays(CloseSaleInDay)

  /**
    * How many tickets we can sell each day
    */
  val dailySellLimit: Int = place match {
    case BigHall => 10
    case SmallHall => 5
  }

  /**
    * Get show's report
    * @param queryDate the query date
    * @return
    */
  def getReport(queryDate: LocalDate): ShowReport = {
    val report = ShowReport(
      title = title,
      genre = genre,
      price = price
    )

    compare(queryDate) match {
      case SaleHasNotStarted =>
        report.copy(
          ticketsLeft = totalTickets,
          ticketsAvailable = 0,
          status = "Sale not started"
        )

      case OpenForSale =>
        val saleReport = OpenForSale.getSaleReport(this, queryDate)

        val statusText = saleReport.ticketsAvailable match {
          case 0 => "Sold out"
          case _ => "Open for sale"
        }

        report.copy(
          ticketsLeft = saleReport.ticketsLeft,
          ticketsAvailable = saleReport.ticketsAvailable,
          status = statusText
        )

      case AllTicketsSoldOut =>
        report.copy(
          status = "Sold out"
        )

      case ShowHasEnded =>
        report.copy(
          status = "In the past"
        )
    }
  }

  /**
    * Compare query date with this show's information
    * @param queryDate
    * @return
    */
  private def compare(queryDate: LocalDate): ShowTimeline = {
    if (queryDate.isBefore(saleStartDate))
      SaleHasNotStarted
    else if (queryDate.isAfterOrEqual(saleStartDate) && queryDate.isBefore(saleCloseDate))
      OpenForSale
    else if (queryDate.isAfterOrEqual(saleCloseDate) && queryDate.isBeforeOrEqual(date))
      AllTicketsSoldOut
    else
      ShowHasEnded
  }

}
