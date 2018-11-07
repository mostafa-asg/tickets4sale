package tickets4sale.core.db.repositories

import scala.collection.mutable
import cats.Id
import tickets4sale.core.db.models._
import cats.implicits._
import tickets4sale.core.util.IdGenerator
import tickets4sale.core.syntax.DateSyntax._

/**
  * In memory datastore using Scala's built-in collections
  */
class InMemoryRepository extends Repository[Id] {

  /**
    * Simulate the `shows` table
    */
  private val shows = new mutable.ListBuffer[Show]

  /**
    * Simulate the `soldTickets` table
    */
  private val soldTickets = new mutable.ListBuffer[SoldTicket]

  /**
    * Find the shows at the given date
    * @param date
    */
  def findShowsAtSpecificDate(date: java.sql.Date): Id[MultiResult[Show]] = Right(
    shows.filter(x => x.openingDay.isBeforeOrEqual(date) &&
                    x.lastDay.isAfterOrEqual(date))
  )

  /**
    * Get the all shows open for sale
    * @param queryDate the query date
    */
  def getAllShowsOpenForSale(queryDate: java.sql.Date): Id[MultiResult[Show]] = Right(
    shows.filter(x => x.saleStartDay.isBeforeOrEqual(queryDate) &&
      x.saleEndDay.isAfterOrEqual(queryDate))
  )

  /**
    * Get all the sold tickets for the given show
    * @param showId show's id
    * @param dayNumber on which day?
    */
  def getSoldTickets(showId: Int, dayNumber: Int): Id[MultiResult[SoldTicket]] = Right(
    soldTickets.filter(x => x.showId === showId && x.dayNumber === dayNumber)
  )

  /**
    * Insert a show to the database
    * @param show
    */
  def insertShow(show: Show): Id[ActionResult] = this.synchronized {
    val newId = IdGenerator.nextId()
    shows += show.copy(id = newId)
    Right(newId)
  }

  /**
    * Insert the sold tickets
    * @param ticket
    */
  def insertSoldTicket(ticket: SoldTicket): Id[ActionResult] = this.synchronized {
    val newId = IdGenerator.nextId()
    soldTickets += ticket.copy(id = newId)
    Right(newId)
  }

}
