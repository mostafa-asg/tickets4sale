package tickets4sale.core.util

object IdGenerator {

  private var id: Int = 0

  def nextId(): Int = this.synchronized {
    id += 1

    id
  }

}
