package org.pico.instances.std

import org.pico.collection.Reduce

package object list {
  implicit object ReduceList extends Reduce[List] {
    override def reduceR[A, B](f: (A, B) => B)(fa: List[A], z: B): B = fa.foldRight(z)(f)
    override def reduceL[A, B](f: (B, A) => B)(z: B, fa: List[A]): B = fa.foldLeft (z)(f)
  }
}
