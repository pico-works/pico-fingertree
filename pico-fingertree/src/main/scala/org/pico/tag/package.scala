package org.pico

import org.pico.fp.@@

import scala.language.higherKinds

package object tag {
  implicit class TaggedF1Ops[V, T, F[_]](val self: F[V @@ T]) extends AnyVal {
    def untagged: F[V] = self.asInstanceOf[F[V]]
  }

  implicit class TaggedF2Ops[V, T, F[_, _], A](val self: F[A, V @@ T]) extends AnyVal {
    def untagged: F[A, V] = self.asInstanceOf[F[A, V]]
  }

  implicit class UntaggedF1Ops[V, F[_]](val self: F[V]) extends AnyVal {
    def tagged[T]: F[V @@ T] = self.asInstanceOf[F[V @@ T]]
  }

  implicit class UntaggedF2Ops[V, F[_, _], A](val self: F[A, V]) extends AnyVal {
    def tagged[T]: F[A, V @@ T] = self.asInstanceOf[F[A, V @@ T]]
  }
}
