package org.pico.collection

import org.pico.fp.@@
import org.pico.syntax.all._
import org.pico.tag.{Tagger, TaggerF}

import scala.language.higherKinds

package object fingertree {
  def !!!(): Nothing = throw new UnsupportedOperationException

  def asList[F[_], A](fa: F[A])(implicit F: Reduce[F]): List[A] = F.reduceR[A, List[A]](_ :: _)(fa, Nil)
  
  def asTree[F[_], V, A](fa: F[A])(implicit F: Reduce[F], M: Measured[V, A]): FingerTree[V, A] = F.reduceR[A, FingerTree[V, A]](_ +: _)(fa, Empty())

  def asList[V, A](digit: Digit[V, A])(implicit F0: Reduce[Digit[V, ?]]): List[A] = {
    type FV[+A] = FingerTree[V, A]
    type DV[+A] = Digit[V, A]
    type NV[+A] = Node[V, A]

    ToReduceOps[DV, A](digit).asList
  }

  def tag[U] = new Tagger[U]
  
  def tagF[U] = new TaggerF[U]
  
  // Manual specialization needed here ... specializing apply above doesn't help
  def tag[U](i: Boolean): Boolean @@ U = i.asInstanceOf[Boolean @@ U]
  def tag[U](i: Char   ): Char    @@ U = i.asInstanceOf[Char    @@ U]
  def tag[U](l: Byte   ): Byte    @@ U = l.asInstanceOf[Byte    @@ U]
  def tag[U](i: Int    ): Int     @@ U = i.asInstanceOf[Int     @@ U]
  def tag[U](l: Long   ): Long    @@ U = l.asInstanceOf[Long    @@ U]
  def tag[U](d: Float  ): Float   @@ U = d.asInstanceOf[Float   @@ U]
  def tag[U](d: Double ): Double  @@ U = d.asInstanceOf[Double  @@ U]
}
