package org.domaintbn.sommd.core.synth

import org.domaintbn.sommd.core.musical.TimelineNote


class AudioRender(val noteData : List<TimelineNote>, tempoFactor :Double = 1.0) {

    val tempoFactor :Double

    //constructor(noteData : Array<TimelineNote>) : this(noteData,1.5)


    val synth : Array<SynthInterface>
    init{
        this.synth = arrayOf(Synths.createLeadSynth(),Synths.createStaccatoSynth(),
            Synths.createSlowSynth(),Synths.createPWMlead())

        this.tempoFactor = 2*tempoFactor //times 2 to get 120 BPM

    }



    fun render(samplingrate: Double) : DoubleArray {
        var maxVal = 1.0 //

        if (this.getTotalTime() == 0.0) {
            throw AudioExportException("Empty song, nothing to render to WAV.")
        }

        if(this.getTotalTime() > 9999.0){
            throw AudioExportException("Don't want to export a song that is this long to audio.")
        }

        val output = DoubleArray(kotlin.math.ceil(this.getTotalTime() * samplingrate).toInt())

        for (tln in this.noteData) {
            if(!isExportable(tln)){
                continue
            }
            val n = NoteEntry(tln,tempoFactor)
            val syn = this.synth[n.instrument % this.synth.size]
            syn.load(n, samplingrate)

            val startSample = (n.location * samplingrate).toInt()
            val endSample = startSample + (n.duration * samplingrate).toInt()

            for (i in startSample until endSample) {
                output[i] += syn.generateNextSample()


            }
        }


        //find maximum sample value in our output
        for (i in output.indices) {
            val newmax = kotlin.math.max(kotlin.math.abs(output[i]), maxVal)
            if (newmax > maxVal) {
                maxVal = newmax
            }

        }

        //normalize
        for (i in output.indices) {
            output[i] *= 1 / maxVal
        }

        //output buffer filled. let's write to file
        //audioBuffer2WAVFile(output, samplingrate, file)
        return output

    }

    private fun isExportable(ne : TimelineNote): Boolean {
        return ne.p.value in 0..127
    }

    fun getTotalTime(): Double {
        var max = 0.0
        for (n in this.noteData) {
            val x = n.location.toDouble() + n.duration.toDouble()
            if (x > max) {
                max = x
            }

        }
        return max*tempoFactor
    }


}
