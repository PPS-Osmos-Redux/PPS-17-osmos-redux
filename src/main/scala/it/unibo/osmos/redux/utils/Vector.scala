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

  /** Vector-point subtraction. Leaves this vector unchanged.
    *
    * @param p point to subtract
    * @return subtraction result as a new instance
    */
  def subtract(p: Point): Vector = Vector(x - p.x, y - p.y)

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

  /** Vector-scalar division. Leaves this vector unchanged.
    *
    * @param v scalar
    * @return division result as a new instance
    */
  def divide(v: Double): Vector = Vector(x / v, y / v)

  /** Gets the module of the vector applying parallelogram law
    *
    * @return module of this vector
    */
  def getLength: Double = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))

  /** Scales this vector module with the desired one
    *
    * @param newLength new length of the module
    * @return a new vector with the specified module
    */
  def getNewLength(newLength: Double): Vector = {
    val temp = newLength / getLength
    Vector(x * temp, y * temp)
  }

  /** Limits the vector's length
    *
    * @param maxLength max length of the vector
    * @return the limited vector
    */
  def limit(maxLength: Double): Vector = {
    if (getLength > maxLength) {
      getNewLength(maxLength)
    } else {
      Vector(x, y)
    }
  }

  /** Vector dot product
    *
    * @param v vector to use for dot product
    * @return dot product
    */
  def dot(v: Vector): Double = (x * v.x) + (y * v.y)

  /** Gets the vector normalized
    *
    * @return this vector normalized as a new instance
    */
  def normalized(): Vector = {
    val length = getLength
    if (length != 0) {
      this.divide(length)
    } else {
      Vector(x, y)
    }
  }
}

object Vector {
  def apply(x: Double, y: Double): Vector = VectorImpl(x, y)

  def zero(): Vector = VectorImpl(0, 0)

  private case class VectorImpl(var _x: Double, var _y: Double) extends Vector {

    override def x: Double = _x

    override def y: Double = _y
  }

}
