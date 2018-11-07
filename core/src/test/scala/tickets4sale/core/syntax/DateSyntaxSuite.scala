package tickets4sale.core.syntax

import java.time.LocalDate

import org.scalatest.{FunSuite, Matchers}

class DateSyntaxSuite extends FunSuite with Matchers {

  import tickets4sale.core.syntax.DateSyntax._

  test("test isBeforeOrEqual for sql.Date") {
    java.sql.Date.valueOf("2018-08-01").isBeforeOrEqual(java.sql.Date.valueOf("2018-08-02")) shouldBe true
    java.sql.Date.valueOf("2018-08-02").isBeforeOrEqual(java.sql.Date.valueOf("2018-08-02")) shouldBe true
    java.sql.Date.valueOf("2018-08-03").isBeforeOrEqual(java.sql.Date.valueOf("2018-08-02")) shouldBe false
  }

  test("test isAfterOrEqual for sql.Date") {
    java.sql.Date.valueOf("2018-08-01").isAfterOrEqual(java.sql.Date.valueOf("2018-08-02")) shouldBe false
    java.sql.Date.valueOf("2018-08-02").isAfterOrEqual(java.sql.Date.valueOf("2018-08-02")) shouldBe true
    java.sql.Date.valueOf("2018-08-03").isAfterOrEqual(java.sql.Date.valueOf("2018-08-02")) shouldBe true
  }

  test("test isBeforeOrEqual for LocalDate") {
    LocalDate.parse("2018-08-01").isBeforeOrEqual(LocalDate.parse("2018-08-02")) shouldBe true
    LocalDate.parse("2018-08-02").isBeforeOrEqual(LocalDate.parse("2018-08-02")) shouldBe true
    LocalDate.parse("2018-08-03").isBeforeOrEqual(LocalDate.parse("2018-08-02")) shouldBe false
  }

  test("test isAfterOrEqual for LocalDate") {
    LocalDate.parse("2018-08-01").isAfterOrEqual(LocalDate.parse("2018-08-02")) shouldBe false
    LocalDate.parse("2018-08-02").isAfterOrEqual(LocalDate.parse("2018-08-02")) shouldBe true
    LocalDate.parse("2018-08-03").isAfterOrEqual(LocalDate.parse("2018-08-02")) shouldBe true
  }

}
