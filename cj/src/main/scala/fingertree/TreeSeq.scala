package fingertree

import scalaz._
import Implicits._
import Syntax._

case class TreeSeq[+A](tree: FingerTree[Int@@Size, A@@Elem]) {
  def size: Int = ToMeasuredOps(tree).measure
}

object TreeSeq {
  def empty: TreeSeq[Nothing] = TreeSeq[Nothing](Empty())
  
  
}