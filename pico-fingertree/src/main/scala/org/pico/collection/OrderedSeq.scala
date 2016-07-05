package org.pico.collection

import org.pico.collection.fingertree.{Elem, FingerTree}
import org.pico.fp.{@@, Monoid, Order, Tag}
import org.pico.fp.syntax._
import org.pico.tag._

sealed trait Key[+K]
case object NoKey extends Key[Nothing]
case class SomeKey[K](key: K) extends Key[K]

trait OrderedSeqImplicits {
  implicit def KeyMonoid[A: Order](): Monoid[Key[A]] = new Monoid[Key[A]] {
    override def zero: Key[A] = NoKey

    override def append(l: Key[A], r: => Key[A]): Key[A] = (l, r) match {
      case (l, NoKey) => l
      case (l, r) => r
    }
  }

  implicit def MeasuredElemSize[K: Order, A] = new Measured[Key[K], A@@Elem] {
    override implicit def monoid: Monoid[Key[K]] = KeyMonoid[K]

    override def measure(tree: A@@Elem): Key[K] = ???
  }
}

case class OrderedSeq[K: Order, +A <: K](tree: FingerTree[Key[K], A]) {
  import OrderedSeq._

  def +:[B >: A <: K](a: B): OrderedSeq[K, B] = {
    OrderedSeq((Tag.of[Elem](a) +: (tree: FingerTree[Key[K], B]).tagged[Elem]).untagged)
  }

  def :+[B >: A <: K](a: B): OrderedSeq[K, B] = {
    OrderedSeq(((tree: FingerTree[Key[K], B]).tagged[Elem] :+ Tag.of[Elem](a)).untagged)
  }
}

object OrderedSeq extends OrderedSeqImplicits
