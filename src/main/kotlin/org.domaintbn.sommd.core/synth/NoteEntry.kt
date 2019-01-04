package org.domaintbn.sommd.core.synth

import org.domaintbn.sommd.core.musical.TimelineNote

class NoteEntry(
    val pitch: Int = -1,
    val velocity: Double = 0.8,
    val location: Double = 0.0,
    val duration : Double = 0.25,
    val instrument : Int = 0
){
    constructor(tln : TimelineNote, tempoMultiplier : Double = 1.0):this(
        tln.p.value,
        tln.velocity.value,
        tln.location.toDouble()*tempoMultiplier,
        tln.duration.toDouble()*tempoMultiplier,
        tln.instrument.value
    )
}
