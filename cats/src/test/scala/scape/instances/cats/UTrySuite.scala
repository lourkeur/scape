package scape.instances.cats

import org.scalacheck._
import cats.kernel.laws.discipline._
import cats.laws.discipline._
import cats.instances.all._
import cats._
import scape._
import scala.util._

given [A: Arbitrary] as Arbitrary[UTry[A]] = Arbitrary {
  Arbitrary.arbitrary[Try[A]].map {
    case Failure(e) => UFailure(e)
    case Success(a) => USuccess(a)
  }
}

given [A: Cogen] as Cogen[UTry[A]] =
  Cogen[Try[A]].contramap {
    case UFailure(e) => Failure(e)
    case USuccess(a) => Success(a)
  }

class UTrySuite extends Properties("UTry Cats instances"):
  given Eq[Throwable] = Eq.fromUniversalEquals
  include(EqTests[UTry[Int]].eqv.all)
  include(MonadErrorTests[UTry, Throwable].monadError[Int, Int, Int].all)
  include(CoflatMapTests[UTry].coflatMap[Int, Int, Int].all)
  include(TraverseTests[UTry].traverse[Int, Int, Int, Set[Int], Option, Option].all)
  include(MonoidTests[UTry[Int]].monoid.all)
