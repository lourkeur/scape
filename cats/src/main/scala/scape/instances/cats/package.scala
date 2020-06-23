package scape.instances.cats

import cats._
import scape._
import util.control.NonFatal
import scala.implicits.Not

given scapeCatsEqForUTry[A: Eq](using Eq[Throwable]) as Eq[UTry[A]]:
  def eqv(x: UTry[A], y: UTry[A]) = (x, y) match
    case (UFailure(e1), UFailure(e2)) => Eq.eqv(e1, e2)
    case (USuccess(a1), USuccess(a2)) => Eq.eqv(a1, a2)
    case _ => false

given scapeCatsInstancesForUTry as MonadError[UTry, Throwable] with CoflatMap[UTry] with Traverse[UTry]:
  def pure[A](a: A): UTry[A] = USuccess(a)

  def flatMap[A, B](x: UTry[A])(f: A => UTry[B]): UTry[B] = x.flatMap(f)
  override def map[A, B](x: UTry[A])(f: A => B): UTry[B] = x.map(f)

  def tailRecM[A, B](a: A)(f: A => UTry[Either[A, B]]): UTry[B] =
    @annotation.tailrec
    def rec(a: A): UTry[B] = f(a) match
      case UFailure(e) => UFailure(e)
      case USuccess(Left(a)) => rec(a)
      case USuccess(Right(b)) => USuccess(b)
    try rec(a) catch case NonFatal(e) => UFailure(e)

  def raiseError[A](e: Throwable): UTry[A] = UFailure(e)

  override def catchNonFatal[A](a: => A)(using Throwable <:< Throwable) = UTry(a)

  def handleErrorWith[A](x: UTry[A])(f: Throwable => UTry[A]): UTry[A] =
    x.recoverWith{ case e => f(e) }

  override def recoverWith[A](x: UTry[A])(pf: PartialFunction[Throwable, UTry[A]]): UTry[A] =
    x.recoverWith(pf)

  override def recover[A](x: UTry[A])(pf: PartialFunction[Throwable, A]): UTry[A] =
    x.recover(pf)

  def coflatMap[A, B](x: UTry[A])(f: UTry[A] => B): UTry[B] = UTry(f(x))

  def foldLeft[A, B](x: UTry[A], b: B)(f: (B, A) => B): B =
    x.fold(_ => b, f(b, _))

  def foldRight[A, B](x: UTry[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
    x.fold(_ => lb, f(_, lb))

  def traverse[F[_]: Applicative, A, B](x: UTry[A])(f: A => F[B]): F[UTry[B]] =
    import cats.syntax.all._
    x match
      case UFailure(e) => UFailure(e).pure
      case USuccess(a) => f(a).map(USuccess(_))
end scapeCatsInstancesForUTry

given scapeCatsSemigroupForUTry[A: Semigroup](using Not[Monoid[A]]) as SemigroupCommon[A] with Semigroup[UTry[A]]:
  val A = Semigroup[A]

given scapeCatsMonoidForUTry[A: Monoid] as SemigroupCommon[A] with Monoid[UTry[A]]:
  val A: Monoid[A] = summon
  def empty = USuccess(A.empty)

private abstract class SemigroupCommon[A] extends Semigroup[UTry[A]]:
  protected val A: Semigroup[A]
  def combine(x: UTry[A], y: UTry[A]) =
    for
      a1 <- x
      a2 <- y
    yield A.combine(a1, a2)

given scapeCatsShowForUTry[A: Show](using Show[Throwable]) as Show[UTry[A]]:
  import cats.syntax.show._
  def show(x: UTry[A]) = x match
    case UFailure(e) => show"UFailure($e)"
    case USuccess(a) => show"USuccess($a)"
