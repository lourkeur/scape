package scape

import org.scalacheck._
import cats.instances.all.{given cats.Eq[?]}

object UTrySuite extends Properties("UTry"):
  include(impl.ImplTest[Int, Int].all)

  property("regression test for #2") =
    USuccess(null).isSuccess
end UTrySuite
