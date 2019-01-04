package org.domaintbn.sommd.core.synth


/**
 * Instance library for Envelope(interface)
 *
 * Simple reuse.
 */

object Envelopes {


    val halfdecayEnv: EnvelopeInterface
        get() = Envelope(1.0, 0.75, 0.5, 0.5)

    val halfriseEnv: EnvelopeInterface
        get() = Envelope(0.5, 0.75, 0.5, 1.0)


    val quicklyDecayingEnv: EnvelopeInterface
        get() {
            val semp = EnvelopeMultipoint()
            semp.addPoint(1.0, 0.0)
            semp.addPoint(1.0, 0.2)
            semp.addPoint(0.3, 0.4)
            semp.addPoint(0.2, 1.0)

            return semp
        }


}//only use static
