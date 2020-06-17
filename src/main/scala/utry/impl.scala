package utry.impl

case class Escape[+A](unescape: A)

type UFailure[+A] = Throwable
type USuccess[+A] = Escape[A] | A
type UTry[+A] = UFailure[A] | USuccess[A]

inline def escape[A](a: A): USuccess[A] = a match
  case _: Escape[_] | _: Throwable => Escape(a)
  case _ => a

// FIXME: inline goes boom
def fold[A, B](ta: UTry[A])(fe: Throwable => B)(fa: A => B) = ta match
  case e: Throwable => fe(e)
  case Escape(a) => fa(a.asInstanceOf[A])
  case a => fa(a.asInstanceOf[A])
