package scape

case class Escape[+A](unescape: A)

trait Impl[E]:
  inline def [A](a: A).escape: A | Escape[A] = a match
    case null | Escape(_) | _: E => Escape(a)
    case _ => a

  inline def [A, B](ta: E | A | Escape[A]).fold(inline fe: E => B)(inline fa: A => B) = ta match
    case e: E => fe(e)
    case Escape(a) => fa(a.asInstanceOf[A])
    case a => fa(a.asInstanceOf[A])
