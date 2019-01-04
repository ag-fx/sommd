package org.domaintbn.sommd.core.parsing

interface IParser {
    fun start(lex: Lexer)
    fun getCommandSequence(): CommandSeq

    val parserSignature: String

}