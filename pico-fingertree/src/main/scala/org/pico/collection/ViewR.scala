package org.pico.collection

import scala.language.higherKinds

sealed trait ViewR[+S[+_], +A]

case object EmptyR extends ViewR[Nothing, Nothing]

case class ConsR[S[+_], +A](sa: S[A], a: A) extends ViewR[S, A]
