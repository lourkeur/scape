package utry.impl

case class WrappedThrowable[+A](val unwrap: A)

type UFailure[+A] = Throwable
type USuccess[+A] = WrappedThrowable[A] | A
type UTry[+A] = UFailure[A] | USuccess[A]

// FIXME: inline goes boom
def [A](a: A).wrap: USuccess[A] = a match
  case _: WrappedThrowable[_] | _: Throwable => WrappedThrowable(a)
  case _ => a

// FIXME: inline goes boom
def [A, B](ta: UTry[A]).fold(fe: Throwable => B)(fa: A => B): B = ta match
  case e: Throwable => fe(e)
  case WrappedThrowable(a) => fa(a.asInstanceOf[A])
  case a => fa(a.asInstanceOf[A])
