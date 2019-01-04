package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*

class ParamYParser : IParser{



    override val parserSignature: String = "ParamY"

    private val percentageUniParser = PercentageUniParser(this)

    private fun buildParamYCommand(): Command {
        return object : Command {
            override val commandType = CommandType.REGULAR

            override fun applyOn(pl: Playhead): List<TimelineNote> {
                pl.loadY(percentageUniParser.data.toList())
                return emptyList()
            }

        }
    }


    override fun getCommandSequence(): CommandSeq {
        if (this.percentageUniParser.data.isEmpty()) {
            error("Meh")
        }
        return CommandSeq(listOf(buildParamYCommand()))
    }


    override fun start(lex: Lexer) {
        percentageUniParser.start(lex)
    }
}