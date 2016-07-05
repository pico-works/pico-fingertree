package org.pico.collection

import org.pico.collection.fingertree.{FingerTree, _}
import org.pico.fp.{@@, Monoid, Tag}
import org.pico.instances.collection.fingertree._
import org.pico.instances.std.seq._
import org.pico.syntax.all._
import org.pico.tag._

trait VectorImplicits {
  implicit object MonoidSize extends Monoid[Int] {
    override def zero: Int = 0
    override def append(a: Int, b: => Int) = a + b
  }

  implicit def MeasuredElemSize[A] = new Measured[Int, A@@Elem] {
    override implicit def monoid: Monoid[Int] = MonoidSize

    override def measure(tree: A@@Elem): Int = 1
  }
}

case class Vector[+A](tree: FingerTree[Int, A]) {
  import Vector._

  def size: Int = ToMeasuredOps(tree.tagged[Elem]).measure

  def splitAt(i: Int): (Vector[A], Vector[A]) = tree.tagged[Elem].split(size < _) match {
    case (l, r) => (Vector(l.untagged), Vector(r.untagged))
  }

  def apply(i: Int): A = Tag unwrap tree.tagged[Elem].splitTree(i < _)(0).a

  def ++[B >: A](that: Vector[B]): Vector[B] = {
    Vector(((this.tree: FingerTree[Int, B]).tagged[Elem] ++ that.tree.tagged[Elem]).untagged)
  }

  def +:[B >: A](a: B): Vector[B] = Vector((Tag.of[Elem](a) +: (tree: FingerTree[Int, B]).tagged[Elem]).untagged)

  def :+[B >: A](a: B): Vector[B] = Vector(((tree: FingerTree[Int, B]).tagged[Elem] :+ Tag.of[Elem](a)).untagged)
}

object Vector extends VectorImplicits {
  val empty: Vector[Nothing] = new Vector[Nothing](Empty())

  def apply[A](values: A*): Vector[A] = {
    new Vector[A](ToReduceOps(tagF[Elem](values)).asTree[Int](MeasuredElemSize[A]).untagged)
  }
}
