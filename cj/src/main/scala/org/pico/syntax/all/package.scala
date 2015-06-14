package org.pico.syntax

import org.pico.syntax.consable.{ToSnocableOps, ToReduceOps, ToMeasuredOps, ToConsableOps}

package object all
    extends ToConsableOps
    with    ToMeasuredOps
    with    ToReduceOps
    with    ToSnocableOps
