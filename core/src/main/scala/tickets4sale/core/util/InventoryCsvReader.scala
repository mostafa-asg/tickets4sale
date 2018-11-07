package tickets4sale.core.util

import cats.effect.IO
import com.github.tototoshi.csv.CSVReader

object InventoryCsvReader {

  type Show = (String,String,String)

  def read(filename: String): IO[List[Show]] = IO {

    val reader = CSVReader.open(filename)
    val result = new scala.collection.mutable.ListBuffer[Show]

    reader.foreach { lineSeq =>
      if (lineSeq.length==3) result += lineToShow(lineSeq)
    }

    result.toList
  }

  private def lineToShow(line: Seq[String]): Show = (line(0),line(1),line(2))

}
