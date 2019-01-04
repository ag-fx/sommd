package org.domaintbn.sommd.core.musical

class SimpleIntegerMath {
    companion object {


        //greatest common factor
        fun gcf(a1: Int, b1: Int): Int {
            var a = kotlin.math.abs(a1)
            var b = kotlin.math.abs(b1)
            while (a != b) {
                if (a > b)
                    a -= b
                else
                    b -= a
            }
            return a
        }

        //least common multiple
        fun lcm(a: Int, b: Int): Int {
            return a * b / gcf(a, b)
        }
    }
}