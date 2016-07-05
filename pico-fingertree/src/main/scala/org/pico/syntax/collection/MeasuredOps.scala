package org.pico.syntax.collection

import org.pico.collection.Measured
import org.pico.fp.syntax.Ops

import scala.language.higherKinds

trait MeasuredOps[V, A] extends Ops[A] {
  implicit def F: Measured[V, A]

  def measure: V = F.measure(self)
}
