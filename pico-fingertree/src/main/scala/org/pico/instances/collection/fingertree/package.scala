package org.pico.instances.collection

import org.pico.collection.fingertree._
import org.pico.collection.{Consable, Measured, Reduce, Snocable}
import org.pico.fp.Monoid
import org.pico.syntax.all._

package object fingertree {
  implicit def ReduceFingerTree[V]: Reduce[FingerTree[V, ?]] = new Reduce[FingerTree[V, ?]] {
    override def reduceR[A, B](f: (A, B) => B)(fa: FingerTree[V, A], z: B): B = {
      implicit val DConsable = Consable(ReduceDigit[V].reduceR(f))
      implicit val FConsable = Consable(ReduceFingerTree[V].reduceR(ReduceNode[V].reduceR(f)))
      fa match {
        case Empty() => z
        case Single(v, a) => f(a, z)
        case Deep(_, l, m: FingerTree[V, Node[V, A]], r) => l +: m +: r +: z
      }
    }

    override def reduceL[A, B](f: (B, A) => B)(z: B, fa: FingerTree[V, A]): B =  {
      implicit val DSnocable = Snocable(ReduceDigit[V].reduceL(f))
      implicit val FSnocable = Snocable(ReduceFingerTree[V].reduceL(ReduceNode[V].reduceL(f)))
      fa match {
        case Empty() => z
        case Single(v, a) => f(z, a)
        case Deep(_, l, m: FingerTree[V, Node[V, A]], r) => z :+ l :+ m :+ r
      }
    }
  }

  implicit def ReduceDigit[V]: Reduce[Digit[V, ?]] = new Reduce[Digit[V, ?]] {
    override def reduceR[A, B](f: (A, B) => B)(fa: Digit[V, A], z: B): B = {
      implicit val BConsable: Consable[A, B] = Consable(f)
      fa match {
        case D0(             ) =>                     z
        case D1(v, a         ) =>                a +: z
        case D2(v, a, b      ) =>           a +: b +: z
        case D3(v, a, b, c   ) =>      a +: b +: c +: z
        case D4(v, a, b, c, d) => a +: b +: c +: d +: z
      }
    }

    override def reduceL[A, B](f: (B, A) => B)(z: B, fa: Digit[V, A]): B = {
      implicit val BSnocable = Snocable(f)
      fa match {
        case D0(             ) => z
        case D1(v, a         ) => z :+ a
        case D2(v, a, b      ) => z :+ a :+ b
        case D3(v, a, b, c   ) => z :+ a :+ b :+ c
        case D4(v, a, b, c, d) => z :+ a :+ b :+ c :+ d
      }
    }
  }

  implicit def ReduceNode[V]: Reduce[Node[V, ?]] = new Reduce[Node[V, ?]] {
    override def reduceR[A, B](f: (A, B) => B)(fa: Node[V, A], z: B): B = {
      implicit val BConsable = Consable(f)
      fa match {
        case N2(v, a, b      ) => a +: b +:      z
        case N3(v, a, b, c   ) => a +: b +: c +: z
      }
    }

    override def reduceL[A, B](f: (B, A) => B)(z: B, fa: Node[V, A]): B = {
      implicit val BSnocable = Snocable[B, A]((sa: B, a: A) => f(sa, a))

      fa match {
        case N2(v, a, b      ) => z :+ a :+ b
        case N3(v, a, b, c   ) => z :+ a :+ b :+ c
      }
    }
  }

  implicit def MeasuredNode[V, A](implicit M: Measured[V, A]): Measured[V, Node[V, A]] = new Measured[V, Node[V, A]] {
    override implicit def monoid: Monoid[V] = M.monoid

    override def measure(n: Node[V, A]): V = n match {
      case N2(v, _, _) => v
      case N3(v, _, _, _) => v
    }
  }

  implicit def MeasuredDigit[V, A](implicit M: Measured[V, A]): Measured[V, Digit[V, A]] = new Measured[V, Digit[V, A]] {
    override implicit def monoid: Monoid[V] = M.monoid

    override def measure(n: Digit[V, A]): V = n match {
      case D0(             ) => M.monoid.zero
      case D1(v, _         ) => v
      case D2(v, _, _      ) => v
      case D3(v, _, _, _   ) => v
      case D4(v, _, _, _, _) => v
    }
  }

  implicit def MeasuredFingerTree[V, A](implicit MD: Measured[V, A]): Measured[V, FingerTree[V, A]] = new Measured[V, FingerTree[V, A]] {
    override implicit def monoid: Monoid[V] = MD.monoid

    override def measure(tree: FingerTree[V, A]): V = tree match {
      case Empty() => monoid.zero
      case Single(v, _) => v
      case Deep(v, _, _, _) => v
    }
  }
}
