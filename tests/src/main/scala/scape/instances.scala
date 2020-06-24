package scape

import org.scalacheck._
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


/** A varied selection of values for good measure law testing */
opaque type Diverse = Int | Boolean | Null | String | UTry[Any/*Diverse*/]

object Diverse:
  given cats.Eq[Diverse] = cats.Eq.fromUniversalEquals

  given Arbitrary[Diverse] = Arbitrary {
    Gen.frequency(
      30 -> Arbitrary.arbitrary[Int],
      5 -> Arbitrary.arbitrary[Boolean],
      5 -> Gen.const(null),
      40 -> Arbitrary.arbitrary[String],
      10 -> Gen.lzy(Arbitrary.arbitrary[UTry[Diverse]]),
    )
  }

  given Cogen[Diverse] = Cogen { (seed, x) =>
    x.match
      case x: Int => Cogen.perturb(seed, x)
      case x: Boolean => Cogen.perturb(seed, x)
      case null => seed
      case x: String => Cogen.perturb(seed, x)
      case x: UTry[Diverse] => Cogen.perturb(seed, x)
  }
