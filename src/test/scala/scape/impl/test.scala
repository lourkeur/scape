package scape

import org.scalacheck._
import Prop.forAll

import cats.kernel.Eq
import cats.kernel.laws.discipline.catsLawsIsEqToProp
import org.typelevel.discipline.Laws

class ImplTest[E: Impl: Eq: Cogen: Arbitrary] extends Laws with ImplLaws[E]:
  def apply[A: Eq: Cogen: Arbitrary, B: Eq: Arbitrary](using Arbitrary[List[Unit]]) =
    new SimpleRuleSet("implementation",
      "correctness of fold on E" -> forAll(foldE[A, B]),
      "correctness of nested escape" -> forAll(escapeFold[A, B]),
    )
