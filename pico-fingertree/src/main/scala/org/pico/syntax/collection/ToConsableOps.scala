package org.pico.syntax.collection

import org.pico.collection.Consable

import scala.language.implicitConversions

trait ToConsableOps {
  implicit class ConstableForIdOps[S](self: S) {
    def +:[A](a: A)(implicit consable: Consable[A, S]): S = consable.cons(a, self)

    def ++:[A](a: A)(implicit consable: Consable[A, S]): S = consable.cons(a, self)
  }
}
