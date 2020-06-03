package utry

import org.scalacheck._
import cats.instances.all.{given cats.Eq[?]}

object UOptionSuite extends Properties("utry"):
  include(impl.ImplTest[Int, Int].all)
