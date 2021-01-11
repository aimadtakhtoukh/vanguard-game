package game.model

trait Field {
  def circle(position : Position): Circle
  def filledCircle(position: Position) : Option[FilledCircle]
  def update(circle : Circle) : Field
  def update(circleOption : Option[Circle]) : Field
}

object Field {
  def apply(): Field = FieldImpl(
    vanguardCircle = EmptyCircle(Vanguard),
    lowerMiddleCircle = EmptyCircle(LowerMiddle),
    upperLeftCircle = EmptyCircle(UpperLeft),
    upperRightCircle = EmptyCircle(UpperRight),
    lowerLeftCircle = EmptyCircle(LowerLeft),
    lowerRightCircle = EmptyCircle(LowerRight)
  )
  private case class FieldImpl(
    vanguardCircle : Circle,
    lowerMiddleCircle : Circle,
    upperLeftCircle : Circle,
    upperRightCircle : Circle,
    lowerLeftCircle : Circle,
    lowerRightCircle : Circle
  ) extends Field {
    def circle(position : Position): Circle =
      position match {
        case Vanguard => vanguardCircle
        case LowerMiddle => lowerMiddleCircle
        case UpperLeft => upperLeftCircle
        case UpperRight => upperRightCircle
        case LowerLeft => lowerLeftCircle
        case LowerRight => lowerRightCircle
      }

    override def filledCircle(position: Position): Option[FilledCircle] = circle(position) match {
      case c : FilledCircle => Some(c)
      case _ : EmptyCircle  => None
    }

    def update(circle : Circle) : Field =
      circle.position match {
        case Vanguard => this.copy(vanguardCircle = circle)
        case LowerMiddle => this.copy(lowerMiddleCircle = circle)
        case UpperLeft => this.copy(upperLeftCircle = circle)
        case UpperRight => this.copy(upperRightCircle = circle)
        case LowerLeft => this.copy(lowerLeftCircle = circle)
        case LowerRight => this.copy(lowerRightCircle = circle)
      }

    override def update(circleOption: Option[Circle]): Field = circleOption.map(this.update).getOrElse(this)
  }
}

sealed trait Circle {val position : Position}
case class EmptyCircle(override val position: Position) extends Circle
case class FilledCircle(position: Position, posture: Posture, card: Card) extends Circle

sealed trait Position
case object Vanguard extends Position
case object UpperLeft extends Position
case object UpperRight extends Position
case object LowerLeft extends Position
case object LowerMiddle extends Position
case object LowerRight extends Position

object Position {
  val values: List[Position] = List(Vanguard, UpperLeft, UpperRight, LowerLeft, LowerMiddle, LowerRight)
}

sealed trait Column {
  val forwardPosition : Position
  val backPosition : Position
}

case object LeftColumn extends Column {
  override val forwardPosition: Position = UpperLeft
  override val backPosition: Position = LowerLeft
}

case object VanguardColumn extends Column {
  override val forwardPosition: Position = Vanguard
  override val backPosition: Position = LowerMiddle
}

case object RightColumn extends Column {
  override val forwardPosition: Position = UpperRight
  override val backPosition: Position = LowerRight
}

sealed trait Posture
case object Rest extends Posture
case object Stand extends Posture