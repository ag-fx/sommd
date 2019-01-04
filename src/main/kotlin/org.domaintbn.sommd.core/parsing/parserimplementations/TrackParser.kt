package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*


class TrackParser() : IParser {

    override val parserSignature: String = "Track"

    val intParser = IntParser(this)

    override fun start(lex: Lexer) {
        val out = intParser.start(lex)
        return
    }

    private fun buildTrackCommand(): Command {
        return object : Command {
            override val commandType = CommandType.REGULAR

            override fun applyOn(pl: Playhead): List<TimelineNote> {
                pl.loadTrack(intParser.data.toList())
                return emptyList()
            }

        }
    }


    override fun getCommandSequence(): CommandSeq {
        if (intParser.data.isEmpty()) {
            error("Meh")
        }
        return CommandSeq(listOf(buildTrackCommand()))
    }

}