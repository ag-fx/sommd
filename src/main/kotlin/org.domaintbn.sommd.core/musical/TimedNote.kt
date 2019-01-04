package org.domaintbn.sommd.core.musical

/**
 * A basic note pre scale-modulation. start time is relative to the given context.
 */
class TimedNote(val pitch: PitchRel, val start: MusicTime, val duration: MusicTime) {
    fun printMe(): String {
        return "'${pitch.oct}'${pitch.idx} s:$start, d:$duration"
    }
}