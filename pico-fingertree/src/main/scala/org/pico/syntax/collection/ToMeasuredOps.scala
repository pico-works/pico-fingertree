package org.pico.syntax.collection

import org.pico.collection.Measured

import scala.language.implicitConversions

trait ToMeasuredOps {
  implicit def ToMeasuredOps[V, A](a: A)(implicit F0: Measured[V, A]) = new MeasuredOps[V, A] {
    def self = a
    implicit def F: Measured[V, A] = F0
  }
}
