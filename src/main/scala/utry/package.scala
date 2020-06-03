package utry

import impl.{wrap, fold}

opaque type UTry[+A] = impl.UTry[A]
private given sub[A] as (impl.UTry[A] <:< UTry[A]) = <:<.refl
private given sup[A] as (UTry[A] <:< impl.UTry[A]) = <:<.refl

object UFailure {
  def apply[A](e: Throwable): UTry[A] = sub(e)
  def unapply[A](ta: UTry[A]): Option[Throwable] =
    ta.fold(e => Some(e))(_ => None)
}

object USuccess {
  def apply[A](a: A): UTry[A] = sub(a.wrap)
  def unapply[A](ta: UTry[A]): Option[A] =
    sup(ta).fold(_ => None)(a => Some(a))
}

object UTry {
  def apply[A](a: => A): UTry[A] =
    try a.wrap
    catch
      case util.control.NonFatal(e) => e
}
