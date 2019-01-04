package org.domaintbn.sommd.core.synth

class LFO(
    private val samplingrate: Double, private val type: LFOShape //swap with oscilator object?
    , private val LFOfreq: Double
) {
    private var oscTime: Double = 0.toDouble()


    val nextValue: Double
        get() {
            val dt = this.oscTime * LFOfreq
            var output = 0.0

            when (this.type) {
                LFO.LFOShape.SIN -> {
                    run {
                        output = this.sineWave(dt)

                    }
                    run {
                        output = this.sawWave(dt)

                    }
                    run { output = this.triangleWave(dt) }
                }
                LFO.LFOShape.SAW -> {
                    run { output = this.sawWave(dt) }
                    run { output = this.triangleWave(dt) }
                }
                LFO.LFOShape.TRI -> {
                    output = this.triangleWave(dt)
                }
            }


            this.oscTime += 1 / this.samplingrate % (1 / LFOfreq * samplingrate)

            return output
        }

    enum class LFOShape {
        SIN, SAW, TRI
    }


    init {
        this.oscTime = 0.0
    }


    private fun sawWave(phase: Double): Double {
        val wrappedPhase = phase % 1
        return -1 + 2 * wrappedPhase
    }

    private fun triangleWave(phase: Double): Double {
        val wrappedPhase = phase % 1
        return 1 - kotlin.math.abs(2 - 4 * wrappedPhase)
    }

    private fun sineWave(phase: Double): Double {
        val wrappedPhase = phase % 1
        return kotlin.math.sin(wrappedPhase * kotlin.math.PI * 2.0)
    }

}
