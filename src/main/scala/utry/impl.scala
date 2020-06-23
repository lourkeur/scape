package utry.impl

case class Escape[+A](unescape: A)

type UBFailure[+E <: Throwable] = E
type UBSuccess[+A] = Escape[A] | A
type UBTry[+E <: Throwable, +A] = UBFailure[E] | UBSuccess[A]

inline def escape[A](a: A): UBSuccess[A] = a match
  case null | _: Escape[_] | _: Throwable => Escape(a)
  case _ => a

// FIXME: inline goes boom
def fold[E <: Throwable, A, B](ta: UBTry[E, A])(fe: E => B)(fa: A => B) = ta match
  case e: Throwable => fe(e.asInstanceOf[E])
  case Escape(a) => fa(a.asInstanceOf[A])
  case a => fa(a.asInstanceOf[A])
