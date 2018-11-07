package tickets4sale.core.db.repositories

import java.sql.Date

import slick.lifted.Tag
import slick.jdbc.H2Profile.api._
import tickets4sale.core.db.models._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Represents an in-memory database usin H2 database
  * All the method's return value is of type `Future`
  * @param db
  */
class H2Repository(db: Database) extends Repository[Future] {

  /**
    * The `shows` table
    * @param tag
    */
  private class ShowTable(tag: Tag) extends Table[Show](tag, "shows") {
    def title = column[String]("title")
    def genre = column[String]("genre")
    def openingDay = column[java.sql.Date]("openingDay")
    def lastDay = column[java.sql.Date]("lastDay")
    def saleStartDay = column[java.sql.Date]("saleStartDay")
    def saleEndDay = column[java.sql.Date]("saleEndDay")
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def * = (title, genre, openingDay, lastDay, saleStartDay, saleEndDay, id) <> (Show.tupled, Show.unapply)
  }
  private val shows = TableQuery[ShowTable]

  /**
    * The `soldTickets` table
    * @param tag
    */
  private class SoldTicketTable(tag : Tag) extends Table[SoldTicket](tag, "soldTickets") {
    def showId = column[Int]("showId")
    def dayNumber = column[Int]("dayNumber")
    def date = column[java.sql.Date]("date")
    def buyer = column[String]("buyer")
    def numberOfTickets = column[Int]("numberOfTickets")
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def show = foreignKey("SOLD_TICKET_FK_SHOW", showId, shows)(_.id)

    def * = (showId, dayNumber, date, buyer, numberOfTickets, id) <> (SoldTicket.tupled, SoldTicket.unapply)
  }
  private val soldTickets = TableQuery[SoldTicketTable]

  /**
    * Create the database's table
    * @return
    */
  def createTables(): Future[Either[Throwable,Unit]] = db.run {
    (shows.schema ++ soldTickets.schema).create
  } map (Right(_)) recover {
    case exc => Left(exc)
  }

  /**
    * Get the all shows open for sale
    * @param queryDate the query date
    */
  def getAllShowsOpenForSale(queryDate: java.sql.Date): Future[MultiResult[Show]] = db.run {
    shows.filter(x => x.saleStartDay <= queryDate &&
                      x.saleEndDay >= queryDate).result
  } map(Right(_)) recover {
    case exc => Left(exc)
  }

  /**
    * Find the shows at the given date
    * @param date
    */
  def findShowsAtSpecificDate(date: Date): Future[MultiResult[Show]] = db.run {
    shows.filter(x => x.openingDay <= date &&
                      x.lastDay >= date).result
  } map(Right(_)) recover {
    case exc => Left(exc)
  }

  /**
    * Get all the sold tickets for the given show
    * @param showId show's id
    * @param dayNumber on which day?
    */
  def getSoldTickets(showId: Int, dayNumber: Int): Future[MultiResult[SoldTicket]] = db.run {
    soldTickets.filter(x => x.showId === showId && x.dayNumber === dayNumber).result
  } map(Right(_)) recover {
    case exc => Left(exc)
  }

  /**
    * Insert a show to the database
    * @param show
    */
  def insertShow(show: Show): Future[ActionResult] = {
    val action = (shows returning shows.map(_.id)) += show
    db.run( action ).map(Right(_)) recover {
      case exc => Left(exc)
    }
  }

  /**
    * Insert the sold tickets
    * @param ticket
    */
  def insertSoldTicket(ticket: SoldTicket): Future[ActionResult] = {
    val action = (soldTickets returning soldTickets.map(_.id)) += ticket
    db.run( action ).map(Right(_)) recover {
      case exc => Left(exc)
    }
  }
}
