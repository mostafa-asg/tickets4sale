package tickets4sale.core.syntax

import java.time.LocalDate
import java.time.temporal.ChronoUnit._

object DateSyntax {

  implicit class SqlDateOps(date: java.sql.Date) {

    def isBeforeOrEqual(anotherDate: java.sql.Date): Boolean = {
      new LocalDateOps(date.toLocalDate).isBeforeOrEqual(anotherDate.toLocalDate)
    }

    def isAfterOrEqual(anotherDate: java.sql.Date): Boolean = {
      new LocalDateOps(date.toLocalDate).isAfterOrEqual(anotherDate.toLocalDate)
    }

    def plusDays(days: Int): java.sql.Date = {
      java.sql.Date.valueOf(
        date.toLocalDate.plusDays(days)
      )
    }

    def minusDays(days: Int): java.sql.Date = {
      java.sql.Date.valueOf(
        date.toLocalDate.minusDays(days)
      )
    }

    def daysBetween(otherDate: java.sql.Date): Long = {
      val date1 = date.toLocalDate
      val date2 = otherDate.toLocalDate

      new LocalDateOps(date1).daysBetween(date2)
    }

  }

  implicit class StringOps(date: String) {

    def toSqlDate(): java.sql.Date = java.sql.Date.valueOf(date)

    def toLocalDate(): java.time.LocalDate = java.time.LocalDate.parse(date)

  }

  implicit class LocalDateOps(date: LocalDate) {

    def isBeforeOrEqual(otherDate: LocalDate): Boolean = {
      date.isBefore(otherDate) || date.isEqual(otherDate)
    }

    def isAfterOrEqual(otherDate: LocalDate): Boolean = {
      date.isAfter(otherDate) || date.isEqual(otherDate)
    }

    def daysBetween(otherDate: LocalDate): Long = {
      DAYS.between(date, otherDate)
    }

  }

}
