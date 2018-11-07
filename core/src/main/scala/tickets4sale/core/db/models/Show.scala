package tickets4sale.core.db.models

/**
  * Represents database model of the `show`
  * @param title show's title
  * @param genre show's genre
  * @param openingDay first day that show will be open
  * @param lastDay the date of the last show's performance
  * @param saleStartDay sale start day
  * @param saleEndDay sale end day
  * @param id the unique id
  */
case class Show(title: String,
                genre: String,
                openingDay: java.sql.Date,
                lastDay:  java.sql.Date,
                saleStartDay: java.sql.Date,
                saleEndDay: java.sql.Date,
                id: Int = 0)
