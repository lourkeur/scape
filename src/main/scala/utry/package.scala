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

object UTry:
  def apply[A](a: => A): UTry[A] =
    try a.wrap
    catch
      case util.control.NonFatal(e) => e

  extension UTryOps on [A](self: UTry[A]):
    def isSuccess = self.fold(_ => false)(_ => true)
    def isFailure = !isSuccess
    def get: A = self.fold(throw _)(a => a)
    def toEither = self.fold(Left(_))(Right(_))
    def toOption = self.fold(_ => None)(Some(_))
    def foreach(f: A => Unit): Unit = self.fold(_ => ())(a => f(a))
    def filter(f: A => Boolean): UTry[A] = self.fold(e => e)(a => if f(a) then self else NoSuchElementException("filtered"))
    def failed: UTry[Throwable] = self.fold(e => e.wrap)(_ => UnsupportedOperationException("Success"))

    // FIXME: should be lazy
    def withFilter(f: A => Boolean) = self.filter(f)

    // FIXME: should be only available on Failure
    def exception = self.fold(e => e)(_ => throw NoSuchElementException("Success"))

  extension UTryMapOps on [A, B](self: UTry[A]):
    def map(f: A => B): UTry[B] = self.fold(e => e)(a => UTry(f(a)))
    def flatMap(f: A => UTry[B]): UTry[B] = self.fold(e => e)(a => try f(a) catch case util.control.NonFatal(e) => e)
    def transform(s: A => UTry[B], f: Throwable => UTry[B]): UTry[B] =
      self.fold(f)(s)

  extension UTryFoldOps on [A, B](self: UTry[A]):
    def fold(fe: (Throwable) => B, fa: (A) => B): B = try impl.fold(self)(fe)(fa) catch case util.control.NonFatal(e) => fe(e)

  extension UTryFlattenOps on [A <: UTry[B], B](self: UTry[A]):
    def flatten: UTry[B] = self.fold(e => e)(a => a)

  extension UTryExtractOps on [A, B >: A](self: UTry[A]):
    def getOrElse(default: => B): B = self.fold(_ => default)(a => a)
    def orElse(default: => UTry[B]): UTry[B] = self.fold(_ => default)(a => self)

    def recover(pf: PartialFunction[Throwable, B]): UTry[B] =
      self.fold({
        case pf(b) => b.wrap
        case _ => self
      })(_ => self)
    def recoverWith(pf: PartialFunction[Throwable, UTry[B]]): UTry[B] =
      self.fold({
        case pf(tb) => tb
        case _ => self
      })(_ => self)
