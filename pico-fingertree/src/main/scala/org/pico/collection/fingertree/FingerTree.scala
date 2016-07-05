package org.pico.collection.fingertree

import org.pico.collection._
import org.pico.fp.syntax._
import org.pico.instances.collection.fingertree._
import org.pico.instances.std.list._
import org.pico.syntax.all._

sealed trait FingerTree[V, +A] {
  type FV[+A] = FingerTree[V, A]
  type DV[+A] = Digit[V, A]

  def +:[B >: A](x: B)(implicit M: Measured[V, B]): FingerTree[V, B] = (this: FingerTree[V, B]) match {
    case Empty()                          => Single(M.measure(x), x)
    case Single(v, y)                     => Deep(D1(x   ), Empty()         , D1(y))
    case Deep(_, D4(_, a, b, c, d), m, r) => Deep(D2(x, a), N3(b, c, d) +: m, r    )
    case Deep(_, l                , m, r) => Deep(x +: l  , m               , r    )
  }

  def :+[B >: A](x: B)(implicit M: Measured[V, B]): FingerTree[V, B] = (this: FingerTree[V, B]) match {
    case Empty()                          => Single(M.measure(x), x)
    case Single(_, x)                     => Deep(D1(x), Empty()         , D1(x   ))
    case Deep(_, l, m, D4(v, a, b, c, d)) => Deep(l    , m :+ N3(a, b, c), D2(d, x))
    case Deep(_, l, m, r                ) => Deep(l    , m               , r :+ x  )
  }
  
  def ++[B >: A](that: FingerTree[V, B])(implicit M: Measured[V, B]): FingerTree[V, B] = FingerTree.append3[V, B](this, Nil, that)
  
  def viewL(implicit M: Measured[V, A]): ViewL[FV, A] = this match {
    case Empty()          => EmptyL
    case Single(v, x)     => ConsL[FV, A](x, Empty())
    case Deep(_, l, m, r) => ConsL[FV, A](l.headL, FingerTree.deepL(l.tailL, m, r))
  }
  
  def viewR(implicit M: Measured[V, A]): ViewR[FV, A] = this match {
    case Empty()          => EmptyR
    case Single(v, x)     => ConsR[FV, A](Empty(), x)
    case Deep(_, l, m, r) => ConsR[FV, A](FingerTree.deepL(l, m, r.tailR), r.headR)
  }

  def splitTree(p: V => Boolean)(i: V)(implicit M: Measured[V, A]): Split[FV, A] = {
    implicit def MonoidV = M.monoid
    this match {
      case Empty()                                    => !!!
      case Single(_, a)                               => Split[FV, A](Empty(), a, Empty())
      case Deep(_, l, m, r) => {
        lazy val vl = i  |+| ToMeasuredOps(l).measure
        lazy val vm = vl |+| ToMeasuredOps(m).measure
        Unit match {
          case _ if p(vl) => (l.split(p)(i): Split[DV, A]) match {
            case Split(ll, lm, lr) => Split[FV, A](ll.asTree[V], lm, FingerTree.deepL(lr, m, r))
          }
          case _ if p(vm) => (m.splitTree(p)(vl): Split[FV, Node[V, A]]) match {
            case Split(ml, mm, mr) => {
              val vml = ToMeasuredOps(ml).measure
              (mm.toDigit.split(p)(vl |+| vml): Split[DV, A]) match {
                case Split(mml, mmm, mmr) => Split[FV, A](FingerTree.deepR(l, ml, mml), mmm, FingerTree.deepL(mmr, mr, r))
              }
            }
          }
          case _ => (r.split(p)(i): Split[DV, A]) match {
            case Split(rl, rm, rr) => Split[FV, A](FingerTree.deepR(l, m, rl), rm, rr.asTree)
          }
        }
      }
    }
  }

  def split(p: V => Boolean)(implicit M: Measured[V, A]): (FingerTree[V, A], FingerTree[V, A]) = this match {
    case Empty() => (Empty(), Empty())
    case _ if p(ToMeasuredOps(this).measure) => splitTree(p)(M.monoid.zero) match {
      case Split(l, m, r) => (l, m +: r)
    }
    case _ => (this, Empty())
  }

  def takeUntil(p: V => Boolean)(implicit M: Measured[V, A]): FingerTree[V, A] = split(p)._1

  def dropUntil(p: V => Boolean)(implicit M: Measured[V, A]): FingerTree[V, A] = split(p)._2
}

case class Empty[V]() extends FingerTree[V, Nothing]

case class Single[V, +A](v: V, a: A) extends FingerTree[V, A]

case class Deep[V, A](v: V, l: Digit[V, A], m: FingerTree[V, Node[V, A]], r: Digit[V, A]) extends FingerTree[V, A]

object Deep {
  def apply[V, A](l: Digit[V, A], m: FingerTree[V, Node[V, A]], r: Digit[V, A])(implicit M: Measured[V, A]): Deep[V, A] = {
    import M.monoid
    Deep(ToMeasuredOps(l).measure |+| ToMeasuredOps(m).measure |+| ToMeasuredOps(r).measure, l, m, r)
  }
}

object FingerTree {
  def deepL[V, A](l: Digit[V, A], m: FingerTree[V, Node[V, A]], r: Digit[V, A])(implicit M: Measured[V, A]): FingerTree[V, A] = l match {
    case D0() => m.viewL match {
      case EmptyL => ToReduceOps[Digit[V, ?], A](r).asTree
      case ConsL(lHead, lTail) => Deep(lHead.toDigit, lTail, r)
    }
    case _ => Deep(l, m, r)
  }
  
  def deepR[V, A](l: Digit[V, A], m: FingerTree[V, Node[V, A]], r: Digit[V, A])(implicit M: Measured[V, A]): FingerTree[V, A] = r match {
    case D0() => m.viewR match {
      case EmptyR => ToReduceOps[Digit[V, ?], A](l).asTree
      case ConsR(rTail, rHead) => Deep(l, rTail, rHead.toDigit)
    }
    case _ => Deep(l, m, r)
  }
  
  def append3[V, A](l: FingerTree[V, A], m: List[A], r: FingerTree[V, A])(implicit M: Measured[V, A]): FingerTree[V, A] = {
    type DV[+A] = Digit[V, A]
    implicit val DConsable: Consable[List[A], FingerTree[V, A]] = Consable(implicitly[Reduce[List]].reduceR(_ +: _))
    implicit val DSnocable: Snocable[FingerTree[V, A], List[A]] = Snocable(implicitly[Reduce[List]].reduceL(_ :+ _))
    (l, m, r) match {
      case (Empty(),             mm, rr                 ) => mm ++: rr
      case (ll,                  mm, Empty()            ) => ll :++ mm
      case (Single(v, x),        mm, rr                 ) => x  +: mm ++: rr
      case (ll,                  mm, Single(v, x)       ) => ll :++ mm :+ x
      case (Deep(_, ll, lm, lr), mm, Deep(_, rl, rm, rr)) => Deep(ll, append3(lm, nodes(lr, mm, rl), rm), rr)
      case _                                              => !!!
    }
  }
  
  def nodes[V, A](l: Digit[V, A], m: List[A], r: Digit[V, A])(implicit M: Measured[V, A]): List[Node[V, A]] = {
    type DV[+A] = Digit[V, A]
    nodes(ToReduceOps[DV, A](l).asList ::: m ::: ToReduceOps[DV, A](r).asList)
  }
  
  def nodes[V, A](as: List[A])(implicit M: Measured[V, A]): List[Node[V, A]] = as match {
    case a::b       ::Nil => N2(a, b   )::Nil
    case a::b::c    ::Nil => N3(a, b, c)::Nil
    case a::b::c::d ::Nil => N2(a, b   )::N2(c, d)::Nil
    case a::b::c    ::xs  => N3(a, b, c)::nodes(xs)
  }
}
