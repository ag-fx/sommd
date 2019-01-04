package org.domaintbn.sommd.gui

import java.io.File
import javax.sound.midi.MidiSystem
import sun.audio.AudioPlayer.player
import javax.sound.midi.MidiEvent
import javax.sound.midi.Sequence
import javax.sound.midi.Sequencer





class MIDITesting{
    fun main(args : Array<String>){
        val x : javax.sound.midi.Synthesizer
        val synth = MidiSystem.getSynthesizer()
    }
}


fun main(args : Array<String>){
    val sequence = MidiSystem.getSequence(File("dragDropMIDI.mid"))


    val sequencer = MidiSystem.getSequencer()
    sequencer.open()
    sequencer.sequence = sequence

    // Start playing
    sequencer.start()

}

