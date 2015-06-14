package org.pico.syntax.consable

import org.pico.collection.Measured

import scala.language.higherKinds
import scalaz.syntax.Ops

trait MeasuredOps[V, A] extends Ops[A] {
  implicit def F: Measured[V, A]

  def measure: V = F.measure(self)
}
