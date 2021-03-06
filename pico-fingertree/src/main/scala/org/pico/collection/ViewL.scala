package org.pico.collection

import scala.language.higherKinds

sealed trait ViewL[+S[+_], +A]

case object EmptyL extends ViewL[Nothing, Nothing]

case class ConsL[+S[+_], +A](a: A, sa: S[A]) extends ViewL[S, A]
