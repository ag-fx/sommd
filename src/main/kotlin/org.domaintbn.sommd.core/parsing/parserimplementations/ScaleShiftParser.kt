package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*


class ScaleShiftParser() : IParser {


    override val parserSignature: String = "ScaleShift"


    private val intParser = IntParser(this)

    private fun buildScaleShiftCommand(): Command {
        return object : Command {
            override val commandType = CommandType.REGULAR

            override fun applyOn(pl: Playhead): List<TimelineNote> {
                pl.loadScaleShift(intParser.data.toList())
                return emptyList()
            }

        }
    }

    override fun getCommandSequence(): CommandSeq {
        if (intParser.data.isEmpty()) {
            error("Meh")
        }
        return CommandSeq(listOf(buildScaleShiftCommand()))
    }


    override fun start(lex: Lexer) {
        val out = intParser.start(lex)
    }

}