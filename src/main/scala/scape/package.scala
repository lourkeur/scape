package scape

import util.control.NonFatal

opaque type UFailure[+A] = Throwable
opaque type USuccess[+A] = A | Escape[A]
opaque type UTry[+A] = UFailure[A] | USuccess[A]

given Impl[Throwable]

object UFailure:
  def apply[A](e: Throwable): UTry[A] = e
  def unapply[A](ta: UTry[A]): Option[Throwable] =
    ta.fold(Some(_))(_ => None)

object USuccess:
  def apply[A](a: A): UTry[A] = a.escape
  def unapply[A](ta: UTry[A]): Option[A] =
    ta.fold(_ => None)(Some[A])

object UTry:
  def apply[A](a: => A): UTry[A] =
    try USuccess[A](a)
    catch
      case NonFatal(e) => UFailure(e)

  export ext.{given _}
end UTry
