package org.pico.fingertree

trait Fv[V] {
  type a[+A] = FingerTree[V, A]
}
