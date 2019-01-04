package org.domaintbn.sommd.core.parsing

import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.parserimplementations.*


class ParsingPlayhead {
    private val playhead: Playhead


    private constructor(playhead: Playhead) {
        this.playhead = playhead
    }

    public constructor() : this(Playhead())


    private val noteOutputLocal = mutableListOf<TimelineNote>()

    val noteOutput: List<TimelineNote> get() = noteOutputLocal.toList()


    fun play(text: String) {
        val np = NoteParser()
        np.start(Lexer(text))
        val sn = np.getCommandSequence().first()
        this.noteOutputLocal.addAll(sn.applyOn(this.playhead))
    }


    fun loadDuration(text: String) {
        val dp = DurationParser()
        dp.start(Lexer(text))
        val dc = dp.getCommandSequence().first()
        dc.applyOn(this.playhead)
    }


    fun loadVelocity(text: String) {
        val vp = VelocityParser()
        vp.start(Lexer(text))
        val ve = vp.getCommandSequence().first()
        ve.applyOn(this.playhead)
    }

    fun loadInstrument(text: String) {
        val ip = InstrumentParser()
        ip.start(Lexer(text))
        val inc = ip.getCommandSequence().first()
        inc.applyOn(this.playhead)
    }

    fun loadScale(text: String) {
        val sp = ScaleParser()
        sp.start(Lexer(text))
        val sc = sp.getCommandSequence().first()
        sc.applyOn(this.playhead)
    }

    fun loadScaleShift(text: String) {
        val ssp = ScaleShiftParser()
        ssp.start(Lexer(text))
        val ss = ssp.getCommandSequence().first()
        ss.applyOn(this.playhead)
    }


    fun getCopy(): ParsingPlayhead {
        return ParsingPlayhead(this.playhead.getCopy())
    }


}