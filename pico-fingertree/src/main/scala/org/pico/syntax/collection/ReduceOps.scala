package org.pico.syntax.collection

import org.pico.collection.fingertree.{Empty, FingerTree}
import org.pico.collection.{Measured, Reduce}
import org.pico.fp.syntax.Ops

import scala.language.higherKinds

trait ReduceOps[F[_], A] extends Ops[F[A]] {
  implicit def F: Reduce[F]

  def asList: List[A] = F.reduceR[A, List[A]](_ :: _)(self, Nil)
  
  def asTree[V](implicit M: Measured[V, A]): FingerTree[V, A] = F.reduceR[A, FingerTree[V, A]](_ +: _)(self, Empty())
}
