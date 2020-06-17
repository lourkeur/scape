package utry.impl

case class WrappedThrowable[+A](val unwrap: A)

type UFailure[+A] = Throwable
type USuccess[+A] = WrappedThrowable[A] | A
type UTry[+A] = UFailure[A] | USuccess[A]

inline def escape[A](a: A): USuccess[A] = a match
  case _: WrappedThrowable[_] | _: Throwable => WrappedThrowable(a)
  case _ => a

// FIXME: inline goes boom
def fold[A, B](ta: UTry[A])(fe: Throwable => B)(fa: A => B) = ta match
  case e: Throwable => fe(e)
  case WrappedThrowable(a) => fa(a.asInstanceOf[A])
  case a => fa(a.asInstanceOf[A])
