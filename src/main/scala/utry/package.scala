package utry

import util.control.NonFatal

opaque type UFailure[+A] <: UTry[A] = impl.UFailure[A]
opaque type USuccess[+A] <: UTry[A] = impl.USuccess[A]
opaque type UTry[+A] = impl.UTry[A]

object UFailure:
  def apply[A](e: Throwable): UTry[A] = e
  def unapply[A](ta: UTry[A]): Option[Throwable] =
    impl.fold(ta)(Some(_))(_ => None)

object USuccess:
  def apply[A](a: A): UTry[A] = impl.escape(a)
  def unapply[A](ta: UTry[A]): Option[A] =
    impl.fold(ta)(_ => None)(Some[A])

object UTry:
  def apply[A](a: => A): UTry[A] =
    try USuccess[A](a)
    catch
      case NonFatal(e) => UFailure(e)

  export ext.{given _}
end UTry
