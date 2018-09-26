package it.unibo.osmos.redux.utils

/** Mixin representing a 2D vector.
  *
  * The methods of this mixin won't alter
  * the values of the classes that extends it.
  */
trait Vector {

  /** Getter for the vector x component
    *
    * @return x component
    */
  def x: Double

  /** Getter for the vector y component
    *
    * @return y component
    */
  def y: Double

  /** Vector addition. Leaves this vector unchanged.
    *
    * @param v vector to add
    * @return addition result as a new instance
    */
  def add(v: Vector): Vector = Vector(x + v.x, y + v.y)

  /** Vector subtraction. Leaves this vector unchanged.
    *
    * @param v vector to subtract
    * @return subtraction result as a new instance
    */
  def subtract(v: Vector): Vector = Vector(x - v.x, y - v.y)

  /** Vector multiplication. Leaves this vector unchanged.
    *
    * @param v vector to multiply
    * @return multiplication result as a new instance
    */
  def multiply(v: Vector): Vector = Vector(x * v.x, y * v.y)

  /** Vector-scalar multiplication. Leaves this vector unchanged.
    *
    * @param v scalar to multiply
    * @return multiplication mu result as a new instance
    */
  def multiply(v: Double): Vector = Vector(x * v, y * v)

  /** Vector dot product
    *
    * @param v vector to use for dot product
    * @return dot product
    */
  def dot(v: Vector): Double = (x * v.x) + (y * v.y)

  /** Limits the vector's magnitude if it is greater than the given one
    *
    * @param maxMagnitude the max magnitude of the vector
    * @return a new vector
    */
  def limit(maxMagnitude: Double): Vector = {
    if (getMagnitude > maxMagnitude) {
      getNewMagnitude(maxMagnitude)
    } else {
      Vector(x, y)
    }
  }

  /** Scales this vector magnitude (module) with the desired one
    *
    * @param desiredMagnitude desired vector magnitude
    * @return a new vector with the specified module
    */
  def getNewMagnitude(desiredMagnitude: Double): Vector = {
    val scale = desiredMagnitude / getMagnitude
    Vector(x * scale, y * scale)
  }

  /** Gets the magnitude (module) of the vector applying parallelogram law
    *
    * @return magnitude of this vector
    */
  def getMagnitude: Double = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))

  /** Gets the vector normalized
    *
    * @return a new normalized instance of this vector
    */
  def normalized(): Vector = {
    val magnitude = getMagnitude
    if (magnitude != 0) {
      this.divide(magnitude)
    } else {
      Vector(x, y)
    }
  }

  /** Vector-scalar division. Leaves this vector unchanged.
    *
    * @param v scalar
    * @return division result as a new instance
    */
  def divide(v: Double): Vector = Vector(x / v, y / v)
}

/** Companion object */
object Vector {
  def apply(x: Double, y: Double): Vector = VectorImpl(x, y)

  def zero(): Vector = VectorImpl(0, 0)

  private case class VectorImpl(override val x: Double, override val y: Double) extends Vector {}

}
