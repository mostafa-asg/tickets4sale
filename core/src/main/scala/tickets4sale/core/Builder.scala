package tickets4sale.core

/**
  * Used to build objects
  * @tparam T
  */
trait Builder[T] {

  def build(): T

}
