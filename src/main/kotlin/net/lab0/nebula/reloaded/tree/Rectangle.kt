package net.lab0.nebula.reloaded.tree

import net.lab0.nebula.reloaded.image.PlanCoordinates

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
    get() = (minY + maxY) / 2.0

  val width
    get() = maxX - minX

  val height
    get() = maxY - minY

  val surface
    get() = width * height

  val xRange
    get() = minX to maxX

  val yRange
    get() = minY to maxY

  /**
   * Values of X in the range minX (included), maxX (included) and `splits - 2` values in between. The value of split must be >= 1
   */
  fun listOfX(splits: Int): List<Double> {
    val step = width / splits
    return listOf(minX) +
        (1 until splits).map { minX + step * it } +
        listOf(maxX)
  }

  /**
   * Values of X in the range minX (included), maxX (included) and `splits - 2` values in between. The value of split must be >= 1
   */
  fun listOfY(splits: Int): List<Double> {
    val step = height / splits
    return listOf(minY) +
        (1 until splits).map { minY + step * it } +
        listOf(maxY)
  }

  val topLeft
    get() = PlanCoordinates(minX, maxY)

  val topRight
    get() = PlanCoordinates(maxX, maxY)

  val bottomLeft
    get() = PlanCoordinates(minX, minY)

  val bottomRight
    get() = PlanCoordinates(maxX, minY)

  val center
    get() = PlanCoordinates(midX, midY)

  val corners
    get() = listOf(
        topLeft, topRight, bottomLeft, bottomRight
    )

  /**
   * @return `true` if the point is inside this rectangle
   */
  fun contains(coordinates: PlanCoordinates): Boolean {
    with(coordinates) {
      return real >= minX && real < maxX && img >= minY && img < maxY
    }
  }

//  fun overlaps(other: Rectangle) =
//      other.corners.any { this.contains(it) } ||
//          this.corners.any { other.contains(it) }


  fun overlaps(other: Rectangle) =
      this.minX < other.maxX && this.maxX > other.minX &&
          this.maxY > other.minY && this.minY < other.maxY
}

