package tickets4sale.core.model

import java.time.LocalDate

import org.scalatest.{FunSuite, Matchers}
import tickets4sale.core.model.places.{BigHall, SmallHall}
import tickets4sale.core.model.report.ShowReport
import tickets4sale.core.Constants._

import scala.collection.mutable

class ShowSuite extends FunSuite with Matchers {

  test("Test show report without buy any tickets") {

    val showTitle = "BARBARIANS"
    val genre = "DRAMA"
    val price = DramaPrice

    val show = Show(
      title = showTitle,
      genre = genre,
      SmallHall,
      dayNumber = 1,
      date = LocalDate.parse("2018-06-01")
    )

    val reports = new mutable.ListBuffer[ShowReport]

    // Generate reports with different query-date
    for (day <- 6 to 31) {
      val date = "2018-05-" + "%02d".format(day)
      reports += show.getReport(queryDate = LocalDate.parse(date))
    }
    for (day <- 1 to 3) {
      val date = "2018-06-" + "%02d".format(day)
      reports += show.getReport(queryDate = LocalDate.parse(date))
    }

    val saleNotStarted = ShowReport(showTitle, genre, price, 100, 0, "Sale not started")
    val soldOut = ShowReport(showTitle, genre, price, 0, 0, "Sold out")
    val inThePast = ShowReport(showTitle, genre, price, 0, 0, "In the past")

    show.saleStartDate shouldBe LocalDate.parse("2018-05-08")
    show.saleCloseDate shouldBe LocalDate.parse("2018-05-28")
    show.dailySellLimit shouldBe 5

    // query date = 2018-05-06
    reports.head shouldBe saleNotStarted

    // query date = 2018-05-07
    reports(1) shouldBe saleNotStarted

    // query date = 2018-05-08
    reports(2) shouldBe ShowReport(showTitle, genre, price, 100, 5, "Open for sale")

    // query date = 2018-05-09
    reports(3) shouldBe ShowReport(showTitle, genre, price, 95, 5, "Open for sale")

    // query date = 2018-05-10
    reports(4) shouldBe ShowReport(showTitle, genre, price, 90, 5, "Open for sale")

    // query date = 2018-05-11
    reports(5) shouldBe ShowReport(showTitle, genre, price, 85, 5, "Open for sale")

    // query date = 2018-05-12
    reports(6) shouldBe ShowReport(showTitle, genre, price, 80, 5, "Open for sale")

    // query date = 2018-05-13
    reports(7) shouldBe ShowReport(showTitle, genre, price, 75, 5, "Open for sale")

    // query date = 2018-05-14
    reports(8) shouldBe ShowReport(showTitle, genre, price, 70, 5, "Open for sale")

    // query date = 2018-05-15
    reports(9) shouldBe ShowReport(showTitle, genre, price, 65, 5, "Open for sale")

    // query date = 2018-05-16
    reports(10) shouldBe ShowReport(showTitle, genre, price, 60, 5, "Open for sale")

    // query date = 2018-05-17
    reports(11) shouldBe ShowReport(showTitle, genre, price, 55, 5, "Open for sale")

    // query date = 2018-05-18
    reports(12) shouldBe ShowReport(showTitle, genre, price, 50, 5, "Open for sale")

    // query date = 2018-05-19
    reports(13) shouldBe ShowReport(showTitle, genre, price, 45, 5, "Open for sale")

    // query date = 2018-05-20
    reports(14) shouldBe ShowReport(showTitle, genre, price, 40, 5, "Open for sale")

    // query date = 2018-05-21
    reports(15) shouldBe ShowReport(showTitle, genre, price, 35, 5, "Open for sale")

    // query date = 2018-05-22
    reports(16) shouldBe ShowReport(showTitle, genre, price, 30, 5, "Open for sale")

    // query date = 2018-05-23
    reports(17) shouldBe ShowReport(showTitle, genre, price, 25, 5, "Open for sale")

    // query date = 2018-05-24
    reports(18) shouldBe ShowReport(showTitle, genre, price, 20, 5, "Open for sale")

    // query date = 2018-05-25
    reports(19) shouldBe ShowReport(showTitle, genre, price, 15, 5, "Open for sale")

    // query date = 2018-05-26
    reports(20) shouldBe ShowReport(showTitle, genre, price, 10, 5, "Open for sale")

    // query date = 2018-05-27
    reports(21) shouldBe ShowReport(showTitle, genre, price, 5, 5, "Open for sale")

    // query date = 2018-05-28
    reports(22) shouldBe soldOut

    // query date = 2018-05-29
    reports(23) shouldBe soldOut

    // query date = 2018-05-30
    reports(24) shouldBe soldOut

    // query date = 2018-05-31
    reports(25) shouldBe soldOut

    // query date = 2018-06-01
    reports(26) shouldBe soldOut

    // query date = 2018-06-02
    reports(27) shouldBe inThePast

    // query date = 2018-06-03
    reports(28) shouldBe inThePast
  }

  // ---------------------------------------------------------------------------------
  test("Test show report with buy tickets") {

    val showTitle = "HARLEQUINADE / ALL ON HER OWN (Double Bill)"
    val genre = "COMEDY"
    val price = ComedyPrice

    val show = Show(
      title = showTitle,
      genre = genre,
      BigHall,
      dayNumber = 1,
      date = LocalDate.parse("2018-05-10"),
      Map(
        LocalDate.parse("2018-05-02") -> List(TicketInfo(purchaser = "Rodolfo", numberOfTickets = 2),
                                              TicketInfo(purchaser = "Mostafa", numberOfTickets = 4)),
        LocalDate.parse("2018-05-01") -> List(TicketInfo(purchaser = "Trump", numberOfTickets = 10))
      )
    )
    val soldTickets = 16

    val reports = new mutable.ListBuffer[ShowReport]

    // Generate reports with different query-date
    for (day <- 14 to 30) {
      val date = "2018-04-" + "%02d".format(day)
      reports += show.getReport(queryDate = LocalDate.parse(date))
    }
    for (day <- 1 to 12) {
      val date = "2018-05-" + "%02d".format(day)
      reports += show.getReport(queryDate = LocalDate.parse(date))
    }

    val saleNotStarted = ShowReport(showTitle, genre, price, 200, 0, "Sale not started")
    val soldOut = ShowReport(showTitle, genre, price, 0, 0, "Sold out")
    val inThePast = ShowReport(showTitle, genre, price, 0, 0, "In the past")

    show.saleStartDate shouldBe LocalDate.parse("2018-04-16")
    show.saleCloseDate shouldBe LocalDate.parse("2018-05-06")
    show.dailySellLimit shouldBe 10

    // query date = 2018-04-14
    reports.head shouldBe saleNotStarted

    // query date = 2018-04-15
    reports(1) shouldBe saleNotStarted

    // query date = 2018-04-16
    reports(2) shouldBe ShowReport(showTitle, genre, price, 200-soldTickets, 10, "Open for sale")

    // query date = 2018-04-17
    reports(3) shouldBe ShowReport(showTitle, genre, price, 190-soldTickets, 10, "Open for sale")

    // query date = 2018-04-18
    reports(4) shouldBe ShowReport(showTitle, genre, price, 180-soldTickets, 10, "Open for sale")

    // query date = 2018-04-19
    reports(5) shouldBe ShowReport(showTitle, genre, price, 170-soldTickets, 10, "Open for sale")

    // query date = 2018-04-20
    reports(6) shouldBe ShowReport(showTitle, genre, price, 160-soldTickets, 10, "Open for sale")

    // query date = 2018-04-21
    reports(7) shouldBe ShowReport(showTitle, genre, price, 150-soldTickets, 10, "Open for sale")

    // query date = 2018-04-22
    reports(8) shouldBe ShowReport(showTitle, genre, price, 140-soldTickets, 10, "Open for sale")

    // query date = 2018-04-23
    reports(9) shouldBe ShowReport(showTitle, genre, price, 130-soldTickets, 10, "Open for sale")

    // query date = 2018-04-24
    reports(10) shouldBe ShowReport(showTitle, genre, price, 120-soldTickets, 10, "Open for sale")

    // query date = 2018-04-25
    reports(11) shouldBe ShowReport(showTitle, genre, price, 110-soldTickets, 10, "Open for sale")

    // query date = 2018-04-26
    reports(12) shouldBe ShowReport(showTitle, genre, price, 100-soldTickets, 10, "Open for sale")

    // query date = 2018-04-27
    reports(13) shouldBe ShowReport(showTitle, genre, price, 90-soldTickets, 10, "Open for sale")

    // query date = 2018-04-28
    reports(14) shouldBe ShowReport(showTitle, genre, price, 80-soldTickets, 10, "Open for sale")

    // query date = 2018-04-29
    reports(15) shouldBe ShowReport(showTitle, genre, price, 70-soldTickets, 10, "Open for sale")

    // query date = 2018-04-30
    reports(16) shouldBe ShowReport(showTitle, genre, price, 60-soldTickets, 10, "Open for sale")

    // query date = 2018-05-01
    // Trump has bought all the tickets
    reports(17) shouldBe ShowReport(showTitle, genre, price, 50-10-6, 0, "Sold out")

    // query date = 2018-05-02
    // Rodolfo and Mostafa have bought 6 tickets
    reports(18) shouldBe ShowReport(showTitle, genre, price, 40-6, 10-6, "Open for sale")

    // query date = 2018-05-03
    // we lost 4 tickets from yesterday instead of 10 because we sell 6 tickets yesterday
    reports(19) shouldBe ShowReport(showTitle, genre, price, 30, 10, "Open for sale")

    // query date = 2018-05-04
    reports(20) shouldBe ShowReport(showTitle, genre, price, 20, 10, "Open for sale")

    // query date = 2018-05-05
    reports(21) shouldBe ShowReport(showTitle, genre, price, 10, 10, "Open for sale")

    // query date = 2018-05-06
    reports(22) shouldBe soldOut

    // query date = 2018-05-07
    reports(23) shouldBe soldOut

    // query date = 2018-05-08
    reports(24) shouldBe soldOut

    // query date = 2018-05-09
    reports(25) shouldBe soldOut

    // query date = 2018-05-10
    reports(26) shouldBe soldOut

    // query date = 2018-05-11
    reports(27) shouldBe inThePast

    // query date = 2018-05-12
    reports(28) shouldBe inThePast
  }


}
