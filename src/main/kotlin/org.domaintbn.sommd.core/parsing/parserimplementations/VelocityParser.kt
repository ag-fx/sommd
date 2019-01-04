package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*

import org.domaintbn.sommd.core.parsing.RegexRepo as r

class VelocityParser() : IParser {

    override val parserSignature: String = "Velocity"

    private val percentageUniParser = PercentageUniParser(this)

    private fun buildInstrumentCommand(): Command {
        return object : Command {
            override val commandType = CommandType.REGULAR

            override fun applyOn(pl: Playhead): List<TimelineNote> {
                pl.loadVelocity(percentageUniParser.data.toList())
                return emptyList()
            }

        }
    }


    override fun getCommandSequence(): CommandSeq {
        if (this.percentageUniParser.data.isEmpty()) {
            error("Meh")
        }
        return CommandSeq(listOf(buildInstrumentCommand()))
    }


    override fun start(lex: Lexer) {
        val out = percentageUniParser.start(lex)
        return
    }

}