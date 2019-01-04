package org.domaintbn.sommd.core.synth

import org.domaintbn.sommd.core.musical.TimelineNote


interface SynthInterface {

    val isFullyInitialized: Boolean

    val name: String

    fun load(n : NoteEntry, samplingrate: Double)

    fun generateNextSample(): Double

}