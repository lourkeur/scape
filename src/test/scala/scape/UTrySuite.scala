package scape

import org.scalacheck._
import cats.instances.all.{given cats.Eq[?]}

object UOptionSuite extends Properties("scape"):
  include(impl.ImplTest[Int, Int].all)

  property("regression test for #2") =
    USuccess(null).isSuccess
end UOptionSuite
