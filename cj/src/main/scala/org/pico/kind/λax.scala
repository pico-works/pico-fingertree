package org.pico.kind

import scala.language.higherKinds

trait λax[T[_, _], X] {
  type a[A] = T[A, X]
}
