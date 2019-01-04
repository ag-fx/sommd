package org.domaintbn.sommd.core.musical

/**
 * Relative pitch. can be used to "index a scale" to yield
 * an absolute pitch value
 */
class PitchRel(val oct: Int, val idx: Int, val silenced: Boolean = false) {
//    init {
//        if (oct !in 0..9) error("Octave not in valid range")
//        if (idx !in -16..15) error("Index not in valid range")
//    }


    constructor() : this(0, 0, true)

    constructor(hexPitch: Int, direction: PitchIndexDirection)
            : this(hexPitch.div(16), direction.factor * (hexPitch.rem(16)))

    constructor(hexPitch: Int) : this(hexPitch, PitchIndexDirection.UP)


    fun getDirection() =
        if (idx >= 0) PitchIndexDirection.UP else PitchIndexDirection.DOWN


    companion object {
        val stdOctave = 5
        val CAN_BE_REPEATED_STEPLEN = 0
    }


}