package scape

import instances.cats.{given _}

import org.scalacheck._
import cats.kernel.laws.discipline._
import cats.laws.discipline._
import cats._

object UTrySuite extends Properties("UTry"):
  given Eq[Throwable] = Eq.fromUniversalEquals

  include(EqTests[UTry[Diverse]].eqv.all)
  include(MonadErrorTests[UTry, Throwable].monadError[Diverse, Diverse, Diverse].all)
  include(CoflatMapTests[UTry].coflatMap[Diverse, Diverse, Diverse].all)
  include(TraverseTests[UTry].traverse[Int, Diverse, Diverse, Set[Int], Option, Option].all)
  include(MonoidTests[UTry[String]].monoid.all)

  property("regression test for #2") =
    USuccess(null).isSuccess
end UTrySuite
