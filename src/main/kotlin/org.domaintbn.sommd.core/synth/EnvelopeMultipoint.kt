package org.domaintbn.sommd.core.synth


class EnvelopeMultipoint : EnvelopeInterface {

    private val points: MutableList<Double> // value placement value placement ..
    private var actualPositionLastEntry: Double = 0.toDouble()


    private fun interpolate(
        lval: Double, rval: Double, lpos: Double,
        rpos: Double, midpos: Double
    ): Double {
        val relpos = (midpos - lpos) / (rpos - lpos)
        return (1 - relpos) * lval + relpos * rval
    }

    init {
        this.points = ArrayList()
    }

    fun addPoint(newval: Double, newpos: Double): Boolean {
        if (newval < 0 || newval > 1 || newpos < 0 || newpos > 1) {
            return false
        }

        if (this.points.size == 0) {

            this.points.add(newval)
            this.points.add(0.0) //force first point to position zero
            return true

        }

        if (newpos <= this.actualPositionLastEntry) {
            return false
        }

        this.points[points.size - 1] =
                this.actualPositionLastEntry //put the previously added point in the correct position

        this.points.add(newval)
        this.actualPositionLastEntry = newpos
        this.points.add(1.0) //add this point as the rightmost point for now
        return true
    }


    private fun setLastEntryMovedToEnd(state: Boolean) {
        if (state) {
            this.points[points.size] = 1.0
        } else {
            this.points[points.size] = this.actualPositionLastEntry
        }
    }

    override fun getEnvValue(position: Double): Double {
        if (this.points.size < 4) {
            return 0.0
        }
        if (position >= 1) {
            return this.points[points.size - 1]
        }
        if (position <= 0) {
            return this.points[0]
        }

        var i = 0
        while (i + 3 < points.size) {

            val lpos = points[i + 1]
            val rpos = points[i + 3]

            if (position >= lpos && position < rpos) {
                val lval = points[i]
                val rval = points[i + 2]

                return this.interpolate(lval, rval, lpos, rpos, position)
            }
            i += 2


        }
        return -1.0 //shoulder never reach this place.
    }

    companion object {

//
//        fun main(args: Array<String>) {
//            var semp = EnvelopeMultipoint.increasingEnv
//
//
//            val n = 8
//            for (i in 0 until n) {
//                val frac = i / (n + 0.0)
//                println("out(" + frac + ") = " + semp.getEnvValue(frac))
//            }
//
//            semp = EnvelopeMultipoint.decayingEnv
//            println()
//
//            for (i in 0 until n) {
//                val frac = i / (n + 0.0)
//                println("out(" + frac + ") = " + semp.getEnvValue(frac))
//            }
//        }
//

        val decayingEnv: EnvelopeMultipoint
            get() {
                val semp = EnvelopeMultipoint()
                semp.addPoint(1.0, 0.0)
                semp.addPoint(1.0, 0.2)
                semp.addPoint(0.3, 0.4)
                semp.addPoint(0.2, 1.0)

                return semp
            }

        val increasingEnv: EnvelopeMultipoint
            get() {
                val semp = EnvelopeMultipoint()
                semp.addPoint(0.0, 0.0)
                semp.addPoint(0.5, 0.2)
                semp.addPoint(0.8, 0.4)
                semp.addPoint(1.0, 0.5)
                semp.addPoint(1.0, 1.0)

                return semp
            }
    }

}
