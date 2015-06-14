package org.pico

import scalaz.@@

package object tag {
  implicit class TaggedOps[A, T](self: A @@ T) {
    def covariant[B >: A]: B @@ T = {
      val a: A = ???
      val b: B = a

      ???
    }
  }
}
