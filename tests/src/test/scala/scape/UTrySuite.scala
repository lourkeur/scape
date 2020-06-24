package scape

import instances.cats.{given _}

import org.scalacheck._
import cats.kernel.laws.discipline._
import cats.laws.discipline._
import cats._

object UTrySuite extends Properties("UTry"):
  given Eq[Throwable] = Eq.fromUniversalEquals

  include(EqTests[UTry[Int]].eqv.all)
  include(MonadErrorTests[UTry, Throwable].monadError[Int, Int, Int].all)
  include(CoflatMapTests[UTry].coflatMap[Int, Int, Int].all)
  include(TraverseTests[UTry].traverse[Int, Int, Int, Set[Int], Option, Option].all)
  include(MonoidTests[UTry[Int]].monoid.all)

  property("regression test for #2") =
    USuccess(null).isSuccess
end UTrySuite
