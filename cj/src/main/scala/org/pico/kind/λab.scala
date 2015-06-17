package org.pico.kind

import scala.language.higherKinds

trait λab[T[_, _]] {
  type ab[A, B] = T[A, B]

  type ba[A, B] = T[B, A]
}
