package tickets4sale.core.model.report

case class Inventory(genre: String, shows: List[ShowReport])

object Inventory {

  def fromMap(map: Map[String,List[ShowReport]]): List[Inventory] = {
    val result = new scala.collection.mutable.ListBuffer[Inventory]
    map.foreach( kv => result += Inventory(kv._1,kv._2) )
    result.toList
  }

}