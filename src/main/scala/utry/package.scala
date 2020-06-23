package utry

import util.control.NonFatal

opaque type UBFailure[+E <: Throwable] <: UBTry[E, Nothing] = impl.UBFailure[E]
opaque type UBSuccess[+A] <: UTry[A] = impl.UBSuccess[A]
opaque type UBTry[+E <: Throwable, +A] = impl.UBTry[E, A]
type UTry[+A] = UBTry[Throwable, A]

object UBFailure:
  def apply[E <: Throwable](e: E): UBTry[E, Nothing] = e
  def unapply[E <: Throwable](ta: UBTry[E, Any]): Option[E] =
    impl.fold(ta)(Some(_))(_ => None)

object UBSuccess:
  def apply[A](a: A): UBTry[Nothing, A] = impl.escape(a)
  def unapply[A](ta: UTry[A]): Option[A] =
    impl.fold(ta)(_ => None)(Some[A])

object UFailure:
  def apply(e: Throwable): UTry[Nothing] = UBFailure(e)
  def unapply(ta: UBTry[Throwable, Any]): Option[Throwable] =
    UBFailure.unapply(ta)

object USuccess:
  export UBSuccess.{apply, unapply}

object UTry:
  def apply[A](a: => A): UTry[A] =
    try USuccess[A](a)
    catch
      case NonFatal(e) => UFailure(e)

  export ext.{given _}
end UTry
