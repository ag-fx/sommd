package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.parsing.CommandSeq
import org.domaintbn.sommd.core.parsing.CommandStrings
import org.domaintbn.sommd.core.parsing.IParser
import org.domaintbn.sommd.core.parsing.Lexer
class MultiCommentParser : IParser {
    override val parserSignature: String = this::class.simpleName.toString()
    override fun getCommandSequence(): CommandSeq {
        return CommandSeq()
    }

    override fun start(lex: Lexer) {
        while (lex.hasTokensLeft()) {
            lex.customSeparator = Regex("\\*/")
            val ss = lex.consumeNextToken(consumer= this)
            if ("*/" == ss.string) {
                return
            }
        }


        return
    }

}