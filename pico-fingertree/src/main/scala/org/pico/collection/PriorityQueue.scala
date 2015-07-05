package org.pico.collection

import org.pico.collection.fingertree.{Elem, FingerTree, Split}
import org.pico.instances.collection.fingertree._
import org.pico.syntax.all._

import scalaz.Scalaz._
import scalaz._

sealed trait Priority[+A]
case object MinusInfinity extends Priority[Nothing]
case class SomePriority[+A](value: A) extends Priority[A]

trait PriorityQueueImplicits {
  implicit def PriorityOrder[A: Order](): Order[Priority[A]] = new Order[Priority[A]] {
    override def order(l: Priority[A], r: Priority[A]): Ordering = (l, r) match {
      case (MinusInfinity, MinusInfinity)       => Ordering.EQ
      case (_, MinusInfinity)                   => Ordering.GT
      case (MinusInfinity, _r)                  => Ordering.LT
      case (SomePriority(lp), SomePriority(rp)) => Order[A].order(lp, rp)
    }
  }

  implicit def PriorityMonoid[A: Order](): Monoid[Priority[A]] = new Monoid[Priority[A]] {
    override def zero: Priority[A] = MinusInfinity
    override def append(l: Priority[A], r: => Priority[A]): Priority[A] = (l, r) match {
      case (l, MinusInfinity) => l
      case (MinusInfinity, r) => r
      case (SomePriority(lp), SomePriority(rp)) => SomePriority(lp max rp)
    }
  }

  implicit def MeasuredElemPriority[A: Order] = new Measured[Priority[A], A@@Elem] {
    override implicit def monoid: Monoid[Priority[A]] = PriorityMonoid[A]

    override def measure(elem: A@@Elem): Priority[A] = SomePriority(Tag unwrap elem)
  }
}

case class PriorityQueue[A](tree: FingerTree[Priority[A], A@@Elem]) {
  import PriorityQueue._

  implicit private def order(implicit O: Order[A]) = PriorityOrder[A]

  def extractMax(implicit O: Order[A]): (A, PriorityQueue[A]) = {
    val m =  ToMeasuredOps(tree).measure
    tree.splitTree(m <= _)(MinusInfinity) match {
      case Split(l, x, r) => (Tag unwrap x, PriorityQueue(l ++ r))
    }
  }
}

object PriorityQueue extends PriorityQueueImplicits
