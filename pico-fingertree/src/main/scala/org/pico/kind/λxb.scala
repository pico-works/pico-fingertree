package org.pico.kind

import scala.language.higherKinds

trait λxb[T[_, _], X] {
  type b[B] = T[X, B]
}
