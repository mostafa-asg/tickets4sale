package tickets4sale.core.db.models

/**
  * Store the information of tickets for `show`
  * @param showId the show's id
  * @param dayNumber the show's day number
  * @param date date's of buy
  * @param buyer purchaser
  * @param numberOfTickets how many tickets has been bought
  * @param id the unique id
  */
case class SoldTicket(showId: Int,
                      dayNumber: Int,
                      date: java.sql.Date,
                      buyer: String,
                      numberOfTickets: Int,
                      id: Int = 0)
