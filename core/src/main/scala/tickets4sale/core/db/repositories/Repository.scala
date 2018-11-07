package tickets4sale.core.db.repositories

import tickets4sale.core.db.models._

/**
  * Represnets all the actions that can be performed on the database
  * @tparam F
  */
trait Repository[F[_]] {

  type MultiResult[A] = Either[Throwable, Seq[A]]

  /**
    * The `Right` of this `Either` represents the generated unique ID for inserts
    * And represents the number of rows affected for update and delete
    */
  type ActionResult = Either[Throwable, Int]

  /**
    * Get the all shows open for sale
    * @param queryDate the query date
    */
  def getAllShowsOpenForSale(queryDate: java.sql.Date): F[MultiResult[Show]]

  /**
    * Find the shows at the given date
    * @param date
    */
  def findShowsAtSpecificDate(date: java.sql.Date): F[MultiResult[Show]]

  /**
    * Get all the sold tickets for the given show
    * @param showId show's id
    * @param dayNumber on which day?
    */
  def getSoldTickets(showId: Int, dayNumber: Int): F[MultiResult[SoldTicket]]

  /**
    * Insert a show to the database
    * @param show
    */
  def insertShow(show: Show): F[ActionResult]

  /**
    * Insert the sold tickets
    * @param ticket
    */
  def insertSoldTicket(ticket: SoldTicket): F[ActionResult]

}