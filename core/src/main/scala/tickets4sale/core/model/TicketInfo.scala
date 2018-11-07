package tickets4sale.core.model

/**
  * Encapsulate reservation of a show
  *
  * @param purchaser full name of a buyer
  * @param numberOfTickets how many tickets he have been bought
  */
case class TicketInfo(purchaser: String, numberOfTickets: Int)