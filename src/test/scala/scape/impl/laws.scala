package scape

import cats.kernel.laws.IsEqArrow

trait ImplLaws[E]:
  def foldE[A, B](e: E, fe: E => B)(using Impl[E]) =
    e.fold(fe)(_ => ???) <-> fe(e)
  def escapeFold[A, B](a: A, fa: A => B)(using Impl[E]) =
    a.escape.fold(_ => ???)(fa) <-> fa(a)
