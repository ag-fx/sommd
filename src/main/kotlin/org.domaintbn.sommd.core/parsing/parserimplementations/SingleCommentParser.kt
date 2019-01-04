package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.parsing.CommandSeq
import org.domaintbn.sommd.core.parsing.CommandStrings
import org.domaintbn.sommd.core.parsing.IParser
import org.domaintbn.sommd.core.parsing.Lexer

class SingleCommentParser() : IParser {

    override val parserSignature: String = this::class.simpleName.toString()
    override fun getCommandSequence(): CommandSeq {
        return CommandSeq()
    }


    override fun start(lex: Lexer) {
        //lex.registerEntry(this)


        lex.customSeparator = Regex("\n")
        while (lex.hasTokensLeft()) {
            val ss = lex.consumeNextToken(this)
            if ("\n" in ss.string) {
                return
            }
        }
        return
    }

}