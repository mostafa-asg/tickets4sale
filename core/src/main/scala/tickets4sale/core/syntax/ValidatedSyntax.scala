package tickets4sale.core.syntax

import cats.Show
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO

object ValidatedSyntax {

  implicit class ValidatedOps[A,B](value: Validated[A,B]) {

    def liftToIO(implicit S: Show[A]): IO[Boolean] = value match {
      case Valid(data) => IO.pure(true)
      case Invalid(data) => IO {
        println( S.show(data) )
        false
      }
    }

  }

}
