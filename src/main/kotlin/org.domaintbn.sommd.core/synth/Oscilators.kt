package org.domaintbn.sommd.core.synth

/**
 * Library of Oscilator(interface) implementations
 *
 * For easy reuse
 *
 */
object Oscilators {


    val sawOsc: OscilatorInterface
        get() = object : OscilatorInterface {


            private var oversampler: Oversampler? = null

            init {
                this.oversampler = Oversampler(this, 1)
            }

            override fun getval(phase: Double): Double {
                var wrappedPhase = phase
                wrappedPhase = wrappedPhase % 1
                return -1 + 2 * wrappedPhase
            }

            override fun getval(phase: Double, harmonicPastShannon: Int): Double {

                var acc = 0.0
                val len = 20
                for (i in 0 until len) {
                    val scale = 1.0 / (len * 10)
                    val shift = scale * (len / 2.0 + i * (1.0 / len))
                    acc += this.getval(phase + shift)
                }

                return acc / len
            }


        }


    val triOsc: OscilatorInterface
        get() = object : OscilatorInterface {

            override fun getval(phase: Double): Double {
                var wrappedPhase = phase
                wrappedPhase = wrappedPhase % 1
                return 1 - kotlin.math.abs(2 - 4 * wrappedPhase)
            }

            override fun getval(phase: Double, harmonicPastShannon: Int): Double {
                return 0.0
            }

        }

    val sinOsc: OscilatorInterface
        get() = object : OscilatorInterface {

            override fun getval(phase: Double): Double {
                var wrappedPhase = phase
                wrappedPhase = wrappedPhase % 1
                return kotlin.math.sin(wrappedPhase * kotlin.math.PI * 2.0)
            }

            override fun getval(phase: Double, harmonicPastShannon: Int): Double {

                return 0.0
            }

        }


}//class only meant for static use
