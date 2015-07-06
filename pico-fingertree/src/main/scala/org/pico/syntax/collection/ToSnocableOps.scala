package org.pico.syntax.collection

import org.pico.collection.Snocable

import scala.language.implicitConversions

trait ToSnocableOps {
  implicit class SnocableForIdOps[S](self: S) {
    def :+[A](a: A)(implicit snocable: Snocable[S, A]): S = snocable.snoc(self, a)

    def :++[A](a: A)(implicit snocable: Snocable[S, A]): S = snocable.snoc(self, a)
  }
}
