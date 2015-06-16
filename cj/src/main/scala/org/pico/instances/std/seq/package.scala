package org.pico.instances.std

import org.pico.collection.Reduce

package object seq {
  implicit object ReduceSeq extends Reduce[Seq] {
    override def reduceR[A, B](f: (A, B) => B)(fa: Seq[A], z: B): B = fa.foldRight(z)(f)
    override def reduceL[A, B](f: (B, A) => B)(z: B, fa: Seq[A]): B = fa.foldLeft (z)(f)
  }
}
