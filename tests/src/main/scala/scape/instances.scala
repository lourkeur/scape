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
