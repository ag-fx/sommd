package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*


class InstrumentParser() : IParser {



    override val parserSignature: String = "Instrument"

    override fun start(lex: Lexer) {
        val out = intParser.start(lex)
    }


    private val intParser = IntParser(this)


    private fun buildInstrumentCommand(): Command {
        return object : Command {
            override val commandType = CommandType.REGULAR

            override fun applyOn(pl: Playhead): List<TimelineNote> {
                pl.loadInstrument(intParser.data.toList())
                return emptyList()
            }

        }
    }



    override fun getCommandSequence(): CommandSeq {
        if (intParser.data.isEmpty()) {
            error("Meh")
        }
        return CommandSeq(listOf(buildInstrumentCommand()))
    }


}