package scape

import org.scalacheck._
import cats.instances.all.{given cats.Eq[?]}

object UTrySuite extends Properties("UTry"):
  property("regression test for #2") =
    USuccess(null).isSuccess
end UTrySuite
