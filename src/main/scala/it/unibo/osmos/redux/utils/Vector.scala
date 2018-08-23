package it.unibo.osmos.redux.utils

/** 2D vector */
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

  /** Setter for the vector x component
    *
    * @param newX new x component
    */
  def x_(newX: Double): Unit

  /** Setter for the vector y component
    *
    * @param newY new y component
    */
  def y_(newY: Double): Unit

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

  /** TODO
    *
    * @return
    */
  def getLength: Double = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))

  /** TODO
    *
    * @param newLength
    * @return
    */
  def getNewLength(newLength: Double): Vector = {
    val temp = newLength / getLength
    Vector(x * temp, y * temp)
  }

  /**
    * Limit the vector's length
    * @param maxLength max length of the vector
    * @return the limited vector
    */
  def limit(maxLength: Double): Vector = {
    if (getLength > maxLength) {
      getNewLength(maxLength)
    } else {
      Vector(x,y)
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
      Vector(x / length, y / length)
    } else {
      Vector(x, y)
    }
  }
}

object Vector {
  def apply(x: Double, y: Double): Vector = VectorImpl(x, y)

  private case class VectorImpl(var _x: Double, var _y: Double) extends Vector {

    override def x: Double = _x

    override def y: Double = _y

    override def x_(newX: Double): Unit = _x = newX

    override def y_(newY: Double): Unit = _y = newY
  }

}
