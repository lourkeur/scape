package scape.impl

import cats.kernel.laws.IsEqArrow

trait ImplLaws:
  def foldFailure[A, B](e: Throwable, fe: Throwable => B) = fold(e)(fe)(_ => ???) <-> fe(e)
  def wrapN[A](n: List[Unit], a: A) =
    def rec[T](n: List[Unit], x: T): T = n match
      case _ :: n2 =>
        fold(rec[UTry[T]](n2, escape(x)))(_ => ???)(identity[T])
      case _ => x
    rec(n, a) <-> a
