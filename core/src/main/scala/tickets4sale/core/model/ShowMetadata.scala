package tickets4sale.core.model

import java.time.LocalDate

trait ShowMetadata {

  /**
    * When users can buy tickets
    */
  val saleStartDate: LocalDate

  /**
    * When users cannot buy any tickets anymore
    * In this date we do not sell any tickets or
    * all the tickets has been sold out
    */
  val saleCloseDate: LocalDate

  /**
    * How many tickets we can sell each day
    */
  val dailySellLimit: Int
}
