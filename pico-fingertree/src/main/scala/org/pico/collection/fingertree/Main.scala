package org.pico.collection.fingertree

import org.pico.collection.Measured
import org.pico.fp.Monoid
import org.pico.instances.collection.fingertree._
import org.pico.instances.std.list._
import org.pico.syntax.all._

object Main {
  implicit object MeasuredSize extends Measured[Int, Char] {
    override implicit val monoid: Monoid[Int] = new Monoid[Int] {
      override def zero: Int = 0
      override def append(a: Int, b: => Int): Int = a + b
    }

    override def measure(a: Char): Int = 1
  }

  def main(args: Array[String]): Unit = {
    val x: FingerTree[Int, Char] = Deep(D2('t', 'h'), Empty(), D3('r', 'e', 'e'))
    val y: FingerTree[Int, Char] = {
        Deep(
            D2('t', 'h'),
            Deep(
                D2(N2('i', 's'), N2('i', 's')),
                Empty(),
                D2(N3('n', 'o', 't'), N2('a', 't'))),
            D3('r', 'e', 'e'))
    }
    println(List(1, 2, 3, 4).asList)
    val z: FingerTree[Int, Char] = 't' +: 'h' +: 'i' +: 's' +: 'i' +: 's' +: 'n' +: 'o' +: 't' +: 'a' +: 't' +: 'r' +: 'e' +: 'e' +: Empty[Int]()
    println(y)
    println(z)
    println(('t'::'h'::'i'::'s'::'i'::'s'::'n'::'o'::'t'::'a'::'t'::'r'::'e'::'e'::Nil).asTree[Int])
    println(ToReduceOps[FingerTree[Int, ?], Char](y).asList)
    println(ToMeasuredOps(y).measure)
    println(y.split(_ >= 5))
//    val sentence = Vector('t', 'h', 'i', 's', 'i', 's', 'n', 'o', 't', 'a', 't', 'r', 'e', 'e')
//    println(sentence)
//    for (i <- 0 until 14) {
//      println(sentence(i))
//    }
//    var ft: FingerTree[Int, Char] = Empty[Int]()
//
//    for (i <- 0 until 10000) {
//      ft = 'a' +: ft
//    }
//    
//    println(ft)
//    
//    trait Elem
//    val specialInt: Int @@ Elem = tag[Elem](1)
//    val specialList: List[Int @@ Elem] = tagF[Elem](List(1, 2, 3, 4))
  }
}
