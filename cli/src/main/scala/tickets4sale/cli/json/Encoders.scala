package tickets4sale.cli.json

import io.circe.{Encoder, Json}
import tickets4sale.core.model.report.ShowReport

object Encoders {

  implicit val show: Encoder[ShowReport] = (report: ShowReport) => {
    Json.obj(
      ("title", Json.fromString(report.title)),
      ("tickets left", Json.fromInt(report.ticketsLeft)),
      ("tickets available", Json.fromInt(report.ticketsAvailable)),
      ("status", Json.fromString(report.status))
    )
  }

}
