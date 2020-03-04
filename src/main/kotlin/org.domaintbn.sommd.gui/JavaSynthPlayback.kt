package org.domaintbn.sommd.gui

import org.domaintbn.sommd.core.musical.TimelineNote

import tornadofx.*
import javax.sound.midi.*

class JavaSynthPlayback : Controller(){

    private val javaMidiSeq     = MidiSystem.getSequencer();
    private val javaMidiSeqTrans = javaMidiSeq.getTransmitter();
    private val javaMidiSynth   = MidiSystem.getSynthesizer();
    private val javaMidiSynthRcvr = javaMidiSynth.getReceiver();

    init {
        javaMidiSeqTrans.setReceiver(javaMidiSynthRcvr);
    }

    fun startSequencer(song: List<TimelineNote>, tempoBPM: Int) {
        javaMidiSeq.open()
        javaMidiSynth.open()

        //val soundbank : Soundbank = MidiSystem.getSoundbank(File("soundbank-deluxe.gm"))
        //synth.loadAllInstruments(soundbank)
        javaMidiSeq.tempoInBPM = tempoBPM.toFloat()

        val myseq = buildSequence(song)
        javaMidiSeq.sequence = myseq
        javaMidiSeq.start()


    }

    fun stopSequencer() {
        if (javaMidiSeq.isRunning) {
            javaMidiSeq.stop()
        }

        javaMidiSeq.close()
        javaMidiSynth.close()
    }


    private fun buildSequence(song : List<TimelineNote>): Sequence {
        val myseq = Sequence(Sequence.PPQ, 96)
        val track = myseq.createTrack()

        val ch2instr = listOf(0, 46, 49, 59, 4, 14, 92, 66, 25, 74, 105, 59, 110, 111, 69, 33)
        for (instr in 0..15) {
            track.add(
                MidiEvent(
                    ShortMessage(
                        ShortMessage.PROGRAM_CHANGE + instr,
                        ch2instr[instr],
                        ch2instr[instr]
                    ), 0
                )
            )

        }

        for (note in song.filter{isExportable(it)}) {
            val on = ShortMessage(
                ShortMessage.NOTE_ON + note.instrument.value % 16,
                note.p.value,
                (note.velocity.value * 127).toInt()
            )
            val off = ShortMessage(
                ShortMessage.NOTE_OFF + note.instrument.value % 16,
                note.p.value,
                (note.velocity.value * 127).toInt()
            )

            track.add(MidiEvent(on, (note.location.toDouble() * 96 * 4).toLong()))
            track.add(
                MidiEvent(
                    off,
                    ((note.duration.toDouble() + note.location.toDouble()) * 96 * 4).toLong()
                )
            )
        }
        return myseq
    }

    private fun isExportable(ne : TimelineNote): Boolean {
        return ne.p.value in 0..127
    }

}
