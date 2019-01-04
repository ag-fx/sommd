package org.domaintbn.sommd.core.musical


/**
 * This class represents a playhead that given commands will "write" notes to a timeline.
 * It stores cycles of parameters that are applied to notes as they are written.
 */
class Playhead {


    private var durCycle = DurationCycle()

    var timePos = MusicTime()


    private var velocityCycle: ParamList<ParamPercentUni> = ParamList<ParamPercentUni>(ParamPercentUni(0.8))

    private var instrumentCycle: ParamList<ParamInt> = ParamList(ParamInt(0))

    private var scaleShiftCycle: ParamList<ParamInt> = ParamList(ParamInt(0))

    private var scaleCycle: ParamList<ParamScale> = ParamList(ParamScale((0..11).map { 5 * 12 + it }.toList()))

    private var panCycle: ParamList<ParamPercentBi> = ParamList(
        ParamPercentBi(
            0.0
        )
    )

    private var transposeCycle: ParamList<ParamInt> = ParamList(ParamInt(0))

    private var trackCycle: ParamList<ParamInt> = ParamList(ParamInt(0))


    private var paramXCycle: ParamList<ParamPercentUni> = ParamList<ParamPercentUni>(ParamPercentUni(0.5))

    private var paramYCycle: ParamList<ParamPercentUni> = ParamList<ParamPercentUni>(ParamPercentUni(0.5))


    fun play(stepNoteBlock: StepNoteBlock): List<TimelineNote> {
        val out = mutableListOf<TimelineNote>()


        val timednotes = stepNoteBlock.toTimedNotes(this.durCycle)

        for (x in timednotes.filter { !it.pitch.silenced }) {
            out.add(modulate(x))
        }

        //out.addAll(modulate(timednotes))

        val totTime = timednotes.maxBy { it.start + it.duration }
        if (totTime == null) {
            error("Unexpected")
        }
        val totTime2 = totTime.duration + totTime.start
        this.timePos = timePos + totTime2

        return out.toList()

    }

    private fun modulate(nt: TimedNote): TimelineNote {

        val timeToCheck = nt.start + this.timePos
        val velocity = velocityCycle.paramByTime(timeToCheck)
        val instrument = instrumentCycle.paramByTime(timeToCheck)
        val pan = panCycle.paramByTime(timeToCheck)
        val scale = scaleCycle.paramByTime(timeToCheck)
        val scaleShift = scaleShiftCycle.paramByTime(timeToCheck)
        val track = trackCycle.paramByTime(timeToCheck)
        val transpose = transposeCycle.paramByTime(timeToCheck)

        val paramX = paramXCycle.paramByTime(timeToCheck)
        val paramY = paramYCycle.paramByTime(timeToCheck)

        val pitch = scale.value.applyScale(nt.pitch, scaleShift.value,transpose.value)


        return TimelineNote(pitch, nt.start + this.timePos, nt.duration, velocity, instrument, track, pan,paramX,paramY)
    }


    fun loadDuration(dr: DurationCycle) {
        this.durCycle = dr
    }


    fun loadVelocity(vl: List<ParamPercentUni>, dr: DurationCycle = this.durCycle.getCopy()) {
        this.velocityCycle = ParamList(vl, dr, this.timePos)
    }

    fun loadInstrument(ins: List<ParamInt>, dr: DurationCycle = this.durCycle.getCopy()) {
        this.instrumentCycle = ParamList(ins, dr, this.timePos)
    }


    fun loadScale(sc: List<ParamScale>, dr: DurationCycle = this.durCycle.getCopy()) {
        this.scaleCycle = ParamList(sc, dr, this.timePos)
    }

    fun loadScaleShift(ss: List<ParamInt>, dr: DurationCycle = this.durCycle.getCopy()) {
        this.scaleShiftCycle = ParamList(ss, dr, this.timePos)
    }

    fun loadPan(pn: List<ParamPercentBi>, dr: DurationCycle = this.durCycle.getCopy()) {
        this.panCycle = ParamList(pn, dr, this.timePos)
    }

    fun loadTrack(tk: List<ParamInt>, dr: DurationCycle = this.durCycle.getCopy()) {
        this.trackCycle = ParamList(tk, dr, this.timePos)
    }



    fun loadTranspose(tp : List<ParamInt>, dr : DurationCycle = this.durCycle.getCopy()){
        this.transposeCycle = ParamList(tp,dr,this.timePos)
    }


    fun getCopy(): Playhead {
        return Playhead().apply {
            val orig = this@Playhead
            this.timePos = orig.timePos
            this.velocityCycle = orig.velocityCycle.getCopy()
            this.instrumentCycle = orig.instrumentCycle.getCopy()

            this.scaleCycle = orig.scaleCycle.getCopy()
            this.scaleShiftCycle = orig.scaleShiftCycle.getCopy()

            this.durCycle = orig.durCycle.getCopy()

            this.panCycle = orig.panCycle.getCopy()
            this.transposeCycle = orig.transposeCycle.getCopy()

            this.paramXCycle = orig.paramXCycle.getCopy()
            this.paramYCycle = orig.paramYCycle.getCopy()

        }
    }

    fun loadX(px: List<ParamPercentUni>,dr : DurationCycle = this.durCycle.getCopy()) {
        this.paramXCycle = ParamList(px, dr, this.timePos)
    }

    fun loadY(py: List<ParamPercentUni>,dr : DurationCycle = this.durCycle.getCopy()) {
        this.paramYCycle = ParamList(py, dr, this.timePos)
    }



}
