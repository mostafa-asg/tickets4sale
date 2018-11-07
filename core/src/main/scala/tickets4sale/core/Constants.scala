package tickets4sale.core

object Constants {

  /**
    * Shows always run for exactly 100 days in the theatre
    */
  val ShowsDurationInDay: Int = 100 - 1

  /**
    * Ticket sale starts 25 days before a show starts
    */
  val OpenForSaleInDay: Int = 25 - 1

  /**
    * 5 days before the show starts it is always sold out
    */
  val CloseSaleInDay: Int = 5 - 1

  /**
    * We sell tickets for 20 days
    */
  val SaleDurationInDays = 20

  /**
    * After 80 days, the show price is discounted with 20%
    */
  val Discount = 20

  // PRICES
  val ComedyPrice = 50
  val MusicalPrice = 70
  val DramaPrice = 40
}
