package org.domaintbn.sommd.core.synth

interface OscilatorInterface {

    /**
     *
     * get the value of this oscilator at this phase.
     *
     * @param phase double
     * @return double in [-1,1]
     */
    fun getval(phase: Double): Double

    /**
     * get the value of this oscilator at this phase
     * if too many harmonics are above the shannon frequency,
     * will return a sequence representation of the waveform
     *
     * @param phase double
     * @param harmonicPastShannon the number of the first harmonic to pass the shannon frequency
     * @return double in [-1,1]
     */
    fun getval(phase: Double, harmonicPastShannon: Int): Double

}
