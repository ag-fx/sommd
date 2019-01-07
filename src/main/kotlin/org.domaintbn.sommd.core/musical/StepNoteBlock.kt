package org.domaintbn.sommd.core.musical

/**
 * A group of notes without any notion of time. Are meant to be modulated by a duration cycle
 */
class StepNoteBlock {

    private class StepNoteWithPosition(val pitch : PitchRel, val steplen : Int,  val repeatable : Boolean,val start : Int){
        constructor(stepNote: StepNote, start : Int) :
                this(stepNote.pitch,stepNote.steplen,stepNote.repeatable,start)

        constructor(pitch : PitchRel,steplen: Int,start: Int) :
                this(pitch,steplen,steplen<=1,start)

    }

    private val stepNotes = mutableListOf<StepNoteWithPosition>()

    fun add(pitch: PitchRel, steplen: Int, start :Int) {
        this.stepNotes.add(StepNoteWithPosition(pitch, steplen,start))
    }

    fun add(other : StepNoteBlock,start : Int){
        for(x in other.stepNotes){
            this.add(x.pitch,x.steplen,x.start+start)
        }
    }

    var previousStart = 0
    fun append(pitch: PitchRel,steplen :Int = 1) {
        val nxtfree = this.getNextFreeStep()
        this.add(pitch, steplen,nxtfree)
    }

    fun append(other: StepNoteBlock){
        val nxtfree = this.getNextFreeStep()
        for(x in other.stepNotes){
            this.add(x.pitch,x.steplen,x.start+nxtfree)
        }
    }

    fun stack(other : StepNoteBlock){
        for(x in other.stepNotes){
            this.add(x.pitch,x.steplen,x.start+previousStart)
        }
    }

    fun appendSeq(seq: List<PitchRel>) {
        for (x in seq) {
            append(x)
        }
    }

    fun appendStack(stacked: List<PitchRel>) {
        val nxtfree = this.getNextFreeStep()
        for (x in stacked) {
            this.add(x, 1,nxtfree)
        }
    }

    fun getTotalSteps() : Int{
        val lastNote = this.stepNotes.maxBy{it.start+it.steplen}
        if(lastNote==null) return 0
        return lastNote.start+lastNote.steplen
    }

    private fun getNextFreeStep(): Int {
        val tmp = stepNotes.maxBy { it.start + it.steplen }
        val out = when (tmp) {
            null -> 0
            else -> tmp.start + tmp.steplen
        }
        previousStart = out
        return out
    }


    fun toTimedNotes(durCycle: DurationCycle): MutableList<TimedNote> {
        val out = mutableListOf<TimedNote>()
        val lastStep = this.stepNotes.maxBy { it.start }

        if (lastStep == null) error("Meh")

        val maxval = lastStep.start+lastStep.steplen

        var accTime = MusicTime()
        for (k in 0..maxval-1) {

            val notesAtCurrentStep = this.stepNotes.filter { it.start == k }
            for (x in notesAtCurrentStep) {
//                val tnote = TimedNote(x.pitch, accTime, durCycle.getTime(x.steplen))
//                out.add(tnote)

                var localAcc = MusicTime()

                if(x.repeatable) {

                    for (dur in durCycle.getDurationsStacked(x.steplen)) {
                        val dTimeLocal = dur.mt
                        val tnote = when (dur.isSilent) {
                            true -> TimedNote(PitchRel(), accTime + localAcc, dTimeLocal)
                            else -> TimedNote(x.pitch, accTime + localAcc, dTimeLocal)
                        }
                        localAcc += dTimeLocal
                        out.add(tnote)
                    }
                }else{
                    //merge the whole thing
                    val dTimeLocal =durCycle.getTime(x.steplen)
                    out.add(TimedNote(x.pitch,accTime+localAcc,dTimeLocal))
                    localAcc += dTimeLocal
                }
            }
            val dTime = durCycle.getTime(1)
            durCycle.shiftPointer(1)
            accTime = accTime + dTime
        }

        return out
    }

    fun isEmpty(): Boolean {
        return this.stepNotes.isEmpty()
    }


}
