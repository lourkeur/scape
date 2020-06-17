package utry.ext

import utry._
import util.control.NonFatal

extension UTryOps on [A](self: UTry[A]):
  def isSuccess = self match
    case USuccess(_) => true
    case _ => false

  def isFailure = self match
    case UFailure(_) => true
    case _ => false

  def get: A = self match
    case UFailure(e) => throw e
    case USuccess(a) => a

  def toEither = self match
    case UFailure(e) => Left(e)
    case USuccess(a) => Right(a)

  def toOption = self match
    case UFailure(_) => None
    case USuccess(a) => Some(a)

  def foreach(f: A => Unit): Unit = self match
    case USuccess(a) => f(a)
    case _ =>

  def filter(pred: A => Boolean): UTry[A] = self match
    case USuccess(a) if pred(a) => self
    case UFailure(_) => self
    case _ => UFailure(NoSuchElementException("filtered"))

  def failed: UTry[Throwable] = self match
    case UFailure(e) => USuccess(e)
    case _ => UFailure(UnsupportedOperationException("Success"))

  def exception: Throwable = self match
    case UFailure(e) => e
    case _ => UnsupportedOperationException("Success")

  // FIXME: should be lazy
  def withFilter(f: A => Boolean) = self.filter(f)

  def fold = ext.FoldPartiallyApplied(self)
  def transform = ext.TransformPartiallyApplied(self)
  def getOrElse = ext.GetOrElsePartiallyApplied(self)
  def orElse = ext.OrElsePartiallyApplied(self)
  def map = ext.MapPartiallyApplied(self)
  def flatMap = ext.FlatMapPartiallyApplied(self)

  def recover = ext.RecoverPartiallyApplied(self)
  def recoverWith = ext.RecoverWithPartiallyApplied(self)

extension UTryFlatten on [A](self: UTry[UTry[A]]):
  def flatten: UTry[A] = self match
    case UFailure(e) => UFailure(e)
    case USuccess(a) => a

class FoldPartiallyApplied[A](self: UTry[A]) extends AnyVal:
  def apply[B](fe: Throwable => B, fa: A => B) = self match
    case UFailure(e) => fe(e)
    case USuccess(a) => try fa(a) catch case NonFatal(e) => fe(e)

class TransformPartiallyApplied[A](self: UTry[A]) extends AnyVal:
  def apply[B](fa: A => UTry[B], fe: Throwable => UTry[B]): UTry[B] = self match
    case UFailure(e) => fe(e)
    case USuccess(a) => fa(a)

class GetOrElsePartiallyApplied[A](self: UTry[A]) extends AnyVal:
  def apply[B >: A](default: => B): B = self match
    case UFailure(e) => default
    case USuccess(a) => a

class OrElsePartiallyApplied[A](self: UTry[A]) extends AnyVal:
  def apply[B >: A](default: => UTry[B]): UTry[B] = self match
    case UFailure(e) => default
    case USuccess(a) => USuccess(a)

class MapPartiallyApplied[A](self: UTry[A]) extends AnyVal:
  def apply[B](f: A => B): UTry[B] = self match
    case UFailure(e) => UFailure(e)
    case USuccess(a) => UTry(f(a))

class FlatMapPartiallyApplied[A](self: UTry[A]) extends AnyVal:
  def apply[B](f: A => UTry[B]): UTry[B] = self match
    case UFailure(e) => UFailure(e)
    case USuccess(a) => try f(a) catch case NonFatal(e) => UFailure(e)

class RecoverPartiallyApplied[A](self: UTry[A]) extends AnyVal:
  def apply[B >: A](pf: PartialFunction[Throwable, B]): UTry[B] = self match
    case UFailure(pf(b)) => USuccess(b)
    case _ => self

class RecoverWithPartiallyApplied[A](self: UTry[A]) extends AnyVal:
  def apply[B >: A](pf: PartialFunction[Throwable, UTry[B]]): UTry[B] = self match
    case UFailure(pf(tb)) => tb
    case _ => self
