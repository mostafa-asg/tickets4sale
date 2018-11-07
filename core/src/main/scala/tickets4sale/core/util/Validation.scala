package tickets4sale.core.util

import tickets4sale.core.syntax.BooleanSyntax._
import java.nio.file.{Files, Paths}

import cats.data.Validated
import cats.implicits._

object Validation {

  def hasValidExtension(filename: String, validExtensions: Set[String]): Validated[String,String] = {
    validExtensions.find(extension => filename.endsWith(extension))
                   .toValid("Invalid file extension")
                   .map(_ => filename)
  }

  def fileExists(filename: String): Validated[String,String] = {
    Files.exists(Paths.get(filename))
         .toValid("File does not exist")
         .map(_ => filename)
  }

  def isValidDateFormat(date: String): Validated[String,String] = {
    date.matches("\\d{4}-\\d{2}-\\d{2}")
        .toValid("Invalid date format")
        .map(_ => date)
  }

}
