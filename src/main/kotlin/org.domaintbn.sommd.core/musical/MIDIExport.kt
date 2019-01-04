package org.domaintbn.sommd.core.musical

class MIDIExport {

    val PPQ = 96

    private class MIDINoteDiffTime(
        val dt: Int
        , val pitch: Int
        , val OnNotOff: Boolean
        , val Velocity: Int = 100
        , val Channel: Int = 0
    ) {
        fun toEventBytes(): List<Int> {
            val eventCode = (if (OnNotOff) 0x90 else 0x80) + Channel


            return handleVariableLength(this.dt)
                .plus(listOf<Int>(eventCode, this.pitch, Velocity))
        }

        private fun handleVariableLength(input: Int): List<Int> {
            val output = mutableListOf<Int>()
            var temp = input
            output.add(temp.and(127))
            while (temp > 127) {
                temp = temp.shr(7)
                output.add(temp.and(127).or(128))
            }
            return output.reversed()
        }
    }


    fun notesToMIDIFileBytesWithTracks(notes: List<TimelineNote>): List<Int> {

        val trackSeparation = notes.groupBy { it.track.value }


        val config_filetype = 0x1
        val config_num_chunks = trackSeparation.size
        val header =
            listOf(
                0x4d, 0x54, 0x68, 0x64,
                0, 0, 0, 6,
                0, config_filetype
                , 0, config_num_chunks,
                0, 0x60
            )

        val timesig = listOf(0, 0xFF, 0x58, 0x04, 0x04, 0x02, 0x18, 0x08)
        val tempo = listOf(0, 0xFF, 0x51, 0x03, 0x07, 0xA1, 0x20)

        val trackChunkList = mutableListOf<Int>()
        for (value in trackSeparation.values) {
            val trackNotes = value
            val trackEventsAbsTime = trackNotes.map { convertToMIDI(it) }.flatten()
            val trackEventsdifftime = sortedDiffedEvents(trackEventsAbsTime).map { it.toEventBytes() }.flatten()
            val trackEvents = trackEventsdifftime.plus(listOf(0, 0xFF, 0x2F, 0))

            val trackChunk = listOf(
                0x4D, 0x54, 0x72, 0x6B,
                0, 0, 0, timesig.size + tempo.size + trackEvents.size
            )
            trackChunkList.addAll(trackChunk.plus(listOf(timesig, tempo, trackEvents).flatten()))
        }


        val outBytes = listOf(header, trackChunkList)
            .flatten()


        return outBytes
    }


    fun notesToMidiFileBytes(notes: List<TimelineNote>): List<Int> {
        val config_filetype = 0x1
        val config_num_chunks = 0x1
        val header =
            listOf(
                0x4d, 0x54, 0x68, 0x64,
                0, 0, 0, 6,
                0, config_filetype
                , 0, config_num_chunks,
                0, 0x60
            )

        val timesig = listOf(0, 0xFF, 0x58, 0x04, 0x04, 0x02, 0x18, 0x08)
        val tempo = listOf(0, 0xFF, 0x51, 0x03, 0x07, 0xA1, 0x20)


        val eventsAbsTime = notes.map { convertToMIDI(it) }.flatten()
        val eventsDiffTime = sortedDiffedEvents(eventsAbsTime)
            .map { it.toEventBytes() }.flatten()

        val events = eventsDiffTime.plus(listOf(0, 0xFF, 0x2F, 0))

        val chunk = listOf(
            0x4D, 0x54, 0x72, 0x6B,
            0, 0, 0, timesig.size + tempo.size + events.size
        )

        val outBytes = listOf(header, chunk, timesig, tempo, events)
            .flatten()


        return outBytes
    }


    private fun convertToMIDI(tln: TimelineNote): List<MIDINoteAbsTime> {
        val ticksPerBar = (this.PPQ * 4)
        val onOffTimes = tln.getOnOffTime()

        val tickPos = (onOffTimes.first.toDouble() * ticksPerBar).toLong()
        val tickPosOff = (onOffTimes.second.toDouble() * ticksPerBar).toLong()

        val velocity = (tln.velocity.value * 127).toInt()
        var channel = (tln.instrument.value) % 16
        if (channel < 0) channel += 16

        if(!isExportable(tln)){
            return emptyList()
        }


        val onEvent = MIDINoteAbsTime(tickPos, tln.p.value, true, velocity, channel)
        val offEvent = MIDINoteAbsTime(tickPosOff, tln.p.value, false, velocity, channel)

        return listOf(onEvent, offEvent)
    }

    private fun sortedDiffedEvents(listAbstime: List<MIDINoteAbsTime>): List<MIDINoteDiffTime> {
        val output = mutableListOf<MIDINoteDiffTime>()
        var acc = (0).toLong()
        val sortedThing = listAbstime.sortedBy { it.absTime }
        for (ev in sortedThing) {
            val dt = (ev.absTime - acc).toInt()

            output.add(MIDINoteDiffTime(dt, ev.pitch, ev.OnNotOff, ev.Velocity, ev.Channel))
            acc += dt
        }

        return output.toList()
    }

    private fun isExportable(tln: TimelineNote): Boolean {
        return tln.p.value in 0..127
            && tln.track.value in 0..127


    }



    companion object {
        fun Export(notes: List<TimelineNote>) : ByteArray{
            val me = MIDIExport()
            return me.notesToMIDIFileBytesWithTracks(notes).map{it.toByte()}.toByteArray()
        }

        fun ExportMidiEvents(notes : Collection<TimelineNote>) : List<MIDINoteAbsTime>{
            return notes.map{MIDIExport().convertToMIDI(it)}.flatten()
        }

    }

}