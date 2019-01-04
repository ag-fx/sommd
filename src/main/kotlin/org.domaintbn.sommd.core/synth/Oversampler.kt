package org.domaintbn.sommd.core.synth

import kotlin.math.pow

class Oversampler(private val osc: OscilatorInterface, overSamplingFactor: Int) {

    private var lastphase: Double = 0.toDouble()
    private val oversampling: Int

    init {
        this.lastphase = 0.0
        this.oversampling = (2.0).pow(overSamplingFactor.toDouble()).toInt()

    }


    fun getval(phase: Double): Double {
        var output = 0.0

        for (i in 1..oversampling) {
            val weight = (i + 0.0) / (0.0 + oversampling)
            output += this.osc.getval(phase * weight + lastphase * (1 - weight))
        }
        output = output / oversampling //filter

        this.lastphase = phase
        return output

    }


}
