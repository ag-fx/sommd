package org.domaintbn.sommd.core.synth

/**
 * Instance library for Synth(Interface)
 *
 * For easy reuse
 */
object Synths {


    val sineSynth: SynthInterface
        get() = object : AbstractSynth() {

            override val name: String
                get() = "Sine-synth"

            override fun setup() {
                this.osc = Array<OscilatorInterface>(1,{Oscilators.sinOsc})

            }

            override fun getSample(osctime: Double, durationPercentage: Double): Double {
                val oscphase = this.oscTime * this.freqFromNote(this.n)
                return this.osc!![0].getval(oscphase)
            }

        }

    //				return this.osc[0].getval(oscphase);
    val sawSynth: SynthInterface
        get() = object : AbstractSynth() {


            override val name: String
                get() = "Saw-synth"

            override fun setup() {
                this.osc = arrayOf(Oscilators.sawOsc)
                this.osample = arrayOf(Oversampler(osc!![0], 4))

            }

            override fun getSample(osctime: Double, durationPercentage: Double): Double {
                val oscphase = this.oscTime * this.freqFromNote(this.n)
                return this.osample!![0].getval(oscphase)
            }

        }

    val triSynth: SynthInterface
        get() = object : AbstractSynth() {

            override val name: String
                get() = "Tri-synth"

            override fun setup() {

                this.osc = arrayOf(Oscilators.triOsc)
                this.osample = arrayOf(Oversampler(osc!![0], 2))

            }

            override fun getSample(osctime: Double, durationPercentage: Double): Double {
                val oscphase = this.oscTime * this.freqFromNote(this.n)
                return this.osc!![0].getval(oscphase)
            }

        }


    fun createSlowSynth(): SynthInterface {
        return object : AbstractSynth() {

            override val name: String
                get() = "Slow-synth"

            override fun setup() {
                this.env = arrayOf(EnvelopeMultipoint.increasingEnv,
                    Envelope(0.0, 0.4, 0.8, 0.5)
                )

                this.lfo = arrayOf(LFO(this.samplingrate, LFO.LFOShape.TRI, 6.0))
                this.osc = arrayOf(Oscilators.sawOsc,Oscilators.triOsc)

                val f = FIRFilter.getMovingAverageFilter(8)
                f.setInterpolationTarget(FIRFilter.getAllPass(8))

                this.filt = arrayOf(f)

            }

            override fun getSample(osctime: Double, durationPercentage: Double): Double {

                val oscphase1 = osctime * this.freqFromNote(this.n)
                //				double oscphase2 = osctime*this.freqFromNote(this.n)/2;

                val oscVibrating = oscphase1 + this.lfo!![0].nextValue * 0.027

                var output =
                    0.5 * (this.sawwave(oscphase1) + this.sawwave(oscVibrating + 0.3)) + this.triwave(oscphase1)

                output = this.filt!![0].applyFilter(output, this.env!![1].getEnvValue(durationPercentage))

                output *= this.env!![0].getEnvValue(durationPercentage)

                output *= 0.5 //to make it more similar to the other patches in volume

                return output
            }

            private fun triwave(phase: Double): Double {
                return osc!![1].getval(phase)
            }

            private fun sawwave(phase: Double): Double {
                return osc!![0].getval(phase)
            }

        }
    }

    fun createStaccatoSynth(): SynthInterface {
        return object : AbstractSynth() {


            override val name: String
                get() = "Staccato-synth"


            override fun setup() {

                val env1 = Envelope(1.0, 0.5, 0.1, 0.3)
                val env2 = Envelope(0.0, 0.7, 1.0, 0.8)
                val env3 = EnvelopeMultipoint.decayingEnv


                this.env = arrayOf(env1,env2,env3)

                val lfo1 = LFO(this.samplingrate, LFO.LFOShape.TRI, 6.4)
                val lfo2 = LFO(this.samplingrate, LFO.LFOShape.SIN, 0.8)

                this.lfo = arrayOf(lfo1,lfo2)
                val f1 = FIRFilter(8)
                f1.setInterpolationTarget(FIRFilter.getMovingAverageFilter(8))

                val f2 = FIRFilter.getStrangeFilter(12)
                this.filt = arrayOf(f1,f2)




            }


            override fun getSample(
                osctime: Double,
                durationPercentage: Double
            ): Double {

                val freq = freqFromNote(n)

                val oscphase = osctime * freq //delta time
                //osc
                var output = this.sawWave(oscphase) * this.sawWave(oscphase + 0.3)

                //filter
                output = this.filt!![0].applyFilter(output, env!![1].getEnvValue(durationPercentage))


                //amplitude
                output *= this.env!![0].getEnvValue(durationPercentage)
                return output
            }

        }
    }

    fun createLeadSynth(): SynthInterface {
        return object : AbstractSynth() {


            override val name: String
                get() = "Lead-synth"

            override fun setup() {
                this.filt = arrayOf(FIRFilter.getMovingAverageFilter(2))
            }

            override fun getSample(osctime: Double, durationPercentage: Double): Double {
                val freq = freqFromNote(n)

                val oscphase = this.oscTime * freq //delta time
                val envelope = 1 - durationPercentage / 2

                val LFO1 = kotlin.math.sin(oscTime * 5.0 * 6.38) * 0.08 * kotlin.math.abs(1 - envelope)

                var out =
                    envelope * (0.3 * sawWave(oscphase + LFO1) + 0.5 * triangleWave(oscphase)) + 0.3 * sineWave(oscphase)


                out = this.filt!![0].applyFilter(out)
                return out
            }

        }
    }

    fun createPWMlead(): SynthInterface {
        return object : AbstractSynth() {

            override val name: String
                get() = "PWM-synth"


            override fun setup() {

                this.lfo = arrayOf(LFO(44100.0, LFO.LFOShape.TRI, 0.3))
                this.osc = arrayOf(Oscilators.sawOsc)
                this.env = arrayOf(Envelopes.halfdecayEnv)
                this.filt = arrayOf(FIRFilter.getMovingAverageFilter(2))

                val osample1 = Oversampler(this.osc!![0], 1)
                val osample2 = Oversampler(this.osc!![0], 1)

                this.osample = arrayOf(osample1,osample2)

            }

            override fun getSample(osctime: Double, durationPercentage: Double): Double {
                val oscphase = this.oscTime * this.freqFromNote(this.n) //delta time

                //				double out = osc[0].getval(oscphase)-osc[0].getval(oscphase+0.5+0.3*lfo[0].getNextValue());

                var out = osample!![0].getval(oscphase) - osample!![1].getval(oscphase + 0.5 + 0.3 * lfo!![0].nextValue)


                //				double out = osc[0].getval(oscphase,1)-osc[0].getval(oscphase+0.5+0.3*lfo[0].getNextValue(),1);

                out = 0.25 * out * env!![0].getEnvValue(durationPercentage)
                out = filt!![0].applyFilter(out)
                return out

            }

        }
    }

}//only use static
