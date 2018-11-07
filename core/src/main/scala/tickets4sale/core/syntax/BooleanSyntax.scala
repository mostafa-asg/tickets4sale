package tickets4sale.core.syntax

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}

object BooleanSyntax {

  implicit class BooleanOps[A](value: Boolean) {

    def toValid(whenFalse: => A): Validated[A,Boolean] = value match {
      case true => Valid(true)
      case false => Invalid(whenFalse)
    }

  }

}
