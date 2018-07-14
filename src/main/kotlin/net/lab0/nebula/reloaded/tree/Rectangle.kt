package net.lab0.nebula.reloaded.tree

interface Rectangle {
  val minX: Double
  val maxX: Double
  val minY: Double
  val maxY: Double


  /**
   * Middle in X
   */
  val midX
    get() = (minX + maxX) / 2.0

  /**
   * Middle in Y
   */
  val midY
    get() = (minX + maxX) / 2.0

  val rangeX
    get() = maxX - minX

  val rangeY
    get() = maxY - minY

  /**
   * Values of X in the range minX (included), maxX (included) and `splits - 2` values in between. The value of split must be >= 1
   */
  fun listOfX(splits: Int): List<Double> {
    val step = rangeX / splits
    return listOf(minX) +
        (1 until splits).map { minX + step * it } +
        listOf(maxX)
  }

  /**
   * Values of X in the range minX (included), maxX (included) and `splits - 2` values in between. The value of split must be >= 1
   */
  fun listOfY(splits: Int): List<Double> {
    val step = rangeY / splits
    return listOf(minY) +
        (1 until splits).map { minY + step * it } +
        listOf(maxY)
  }


  val yMinMidMax
    get() = listOf(minY, midY, maxY)
}

