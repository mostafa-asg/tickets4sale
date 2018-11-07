package tickets4sale.core.model.places

sealed trait Place {
  val name: String
  val capacity: Int
}

object BigHall extends Place {
  val name = "BigHall"
  val capacity = 200

  override def toString: String = "Big Hall"
}

object SmallHall extends Place {
  val name = "SmallHall"
  val capacity = 100

  override def toString: String = "Small Hall"
}


