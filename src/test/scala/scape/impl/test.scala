package scape.impl

import org.scalacheck._
import Prop.forAll

import cats.kernel.Eq
import cats.kernel.laws.discipline.catsLawsIsEqToProp
import org.typelevel.discipline.Laws

object ImplTest extends Laws with ImplLaws:
  def apply[A: Eq: Arbitrary, B: Eq: Arbitrary](using Arbitrary[List[Unit]]) =
    new SimpleRuleSet("implementation",
      "correctness of fold on failures" -> forAll(foldFailure[A, B]),
      "correctness of nested wrap" -> forAll(wrapN[A]),
    )
