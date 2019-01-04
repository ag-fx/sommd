package org.domaintbn.sommd.core.synth

import org.domaintbn.sommd.core.musical.MusicTime
import org.domaintbn.sommd.core.musical.PitchAbs
import org.domaintbn.sommd.core.musical.TimelineNote
import kotlin.math.pow


/**
 *
 * To be subclassed to determine how samples are generated
 * Is supplied with three envelopes
 * and two LFOs
 */
abstract class AbstractSynth() : SynthInterface {

    protected var n: NoteEntry = NoteEntry()
    protected var samplingrate: Double = 0.toDouble()

    protected var env: Array<EnvelopeInterface>?
     protected var lfo: Array<LFO>?
     protected var filt: Array<FilterInterface>?
     protected var osc: Array<OscilatorInterface>?
     protected var osample: Array<Oversampler>?

    protected var oscTime: Double = 0.toDouble()
    private var declick: Double = 0.toDouble()


    override val isFullyInitialized: Boolean
        get() {
            var output = true

            output = output and (env != null)
            output = output and (lfo != null)


            return output
        }

    init {

        env = null

        lfo = null

        filt = null

        osc = null

        osample = null

        samplingrate = 44100.0
        this.oscTime = 0.0

        this.setup()

    }


    protected abstract fun setup()


    override fun load(n: NoteEntry, samplingrate: Double) {
        this.n = n
        this.samplingrate = samplingrate
        this.oscTime = 0.0
        this.declick = 0.0


    }

    protected fun sawWave(phase: Double): Double {
        val wrappedPhase = phase % 1
        return -1 + 2 * wrappedPhase
    }

    protected fun triangleWave(phase: Double): Double {
        val wrappedPhase = phase % 1
        return 1 - kotlin.math.abs(2 - 4 * wrappedPhase)
    }

    protected fun sineWave(phase: Double): Double {
        val wrappedPhase = phase % 1
        return kotlin.math.sin(wrappedPhase * kotlin.math.PI * 2.0)
    }


    override fun generateNextSample(): Double {
        val amplitude = this.n.velocity
        val durationPercentage = this.oscTime / this.n.duration.toDouble()

        var output = amplitude * this.getSample(this.oscTime, durationPercentage)



        output *= this.declick

        updateDeclick(durationPercentage)

        this.oscTime += 1 / this.samplingrate

        return output
    }


    private fun updateDeclick(durationPercentage: Double) {
        if (declick < 1 && durationPercentage < 0.5) {
            declick += 0.25
        }

        if (durationPercentage > 0.99) {
            declick = 1 - (durationPercentage - 0.99) / (1 - 0.99)
        }
    }


    abstract fun getSample(osctime: Double, durationPercentage: Double): Double


    protected fun freqFromNote(n: NoteEntry): Double {
        val notediff = n.pitch - 12 * 6
        val power = (2.0).pow (notediff.toDouble() / 12.toDouble())
        val out = power * 440.0
        return out
    }


}
