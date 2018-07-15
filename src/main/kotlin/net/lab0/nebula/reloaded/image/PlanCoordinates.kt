package net.lab0.nebula.reloaded.image


class PlanCoordinates @JvmOverloads constructor(
    val real: Double = 0.0,
    val img: Double = 0.0
) {
  constructor(
      real: Number,
      img: Number
  ) : this(real.toDouble(), img.toDouble())

  /**
   * Vector difference
   */
  operator fun minus(other: PlanCoordinates) =
      PlanCoordinates(this.real - other.real, this.img - other.img)
}
