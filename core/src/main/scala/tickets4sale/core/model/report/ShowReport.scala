package tickets4sale.core.model.report

case class ShowReport(title: String = "",
                      genre: String = "",
                      price: Double = 0,
                      ticketsLeft: Int = 0,
                      ticketsAvailable: Int = 0,
                      status: String = "",
                      isOpenForSale: Boolean = false)
