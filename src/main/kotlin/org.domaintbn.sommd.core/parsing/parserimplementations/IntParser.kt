package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.IpolType
import org.domaintbn.sommd.core.musical.ParamInt
import org.domaintbn.sommd.core.parsing.*

import org.domaintbn.sommd.core.parsing.RegexRepo as r

class IntParser(parent : IParser) : IParser{
    override val parserSignature: String = parent.parserSignature+"/"+"Integer"

    val data = mutableListOf<ParamInt>()

    private fun isRootCommand(str : String) : Boolean{
        return RootCommandValidator.isValidRootCommand(str)
    }



    fun parseInt(s : String) : List<Int>{
        val out = mutableListOf<Int>()
        if("^" in s) {
            val x = s.split("^")
            repeat(x[1].toInt()) {
                out.add(x[0].toInt())
            }
        }else{
            out.add(s.toInt())
        }
        return out
    }

    override fun start(lex: Lexer) {

        //lex.registerEntry(this)
        var keepGoing = true


        while (lex.hasTokensLeft() && keepGoing) {
            lex.customSeparator = r.separatorToEnterGroups
            val tok = lex.peekNextToken()
            when {
                tok.isWhiteSpace() -> {
                    lex.consumeNextToken( consumer = this)
                }
                tok.string == "//" -> {
                    lex.consumeNextToken( consumer =this)
                    SingleCommentParser().apply { start(lex) }
                }


                tok.string == "/*" -> {
                    lex.consumeNextToken( consumer =this)
                    MultiCommentParser().apply { start(lex) }
                }
                tok.string.matches(r.regexInt) -> {
                    lex.consumeNextToken( consumer =this)
                    data.add(ParamInt(tok.string.toInt()))
                }
                tok.string.matches(r.repeatedInt) ->{
                    lex.consumeNextToken(this)
                    val x = tok.string.split("^")
                    repeat(x[1].toInt()){
                        data.add(ParamInt(x[0].toInt()))
                    }
                }
                tok.string == "[" ->{
                    lex.consumeNextToken(this)
                    val group =
                        BracketGroupParser(
                            true,
                            listOf(r.regexInt, r.repeatedInt),
                            r.separatorToLeaveGroups,
                                this
                        )
                    try {
                        group.start(lex)
                    } catch (bpe : BracketParserException){
                        processBadGroupToken(bpe)
                    }
                    processGroup(IpolType.HOLD,group.recursiveStringList)
                }
                tok.string == "-[" ->{
                    lex.consumeNextToken(this)
                    val group =
                        BracketGroupParser(
                            true,
                            listOf(r.regexInt),
                            r.separatorToLeaveGroups
                        ,this
                        )
                    try {
                        group.start(lex)
                    } catch (bpe : BracketParserException){
                        processBadGroupToken(bpe)
                    }
                    processGroup(IpolType.LINEAR,group.recursiveStringList)
                }
                isRootCommand(tok.string) && data.isNotEmpty() ->{
                    return
                }
                else -> {
                    keepGoing = false

                }
            }
        }

        if(!lex.hasTokensLeft() && !this.data.isEmpty()){
            return
        }

        val problemToken = lex.getLastFetchedToken()

        throw ParserException(problemToken,determineError(problemToken.string),this)
    }

    private fun processBadGroupToken(bpe: BracketParserException) {
        //currently no tokens accepted by Intparser that is not allowed
        //in sub bracket groups, so nothing to check
        bpe.source
    }


    override fun getCommandSequence(): CommandSeq {
        error("Do not use!")
    }

    private fun processGroup(ipolType: IpolType, group : RecursiveList<String>){

        val intData = group.content.map { parseInt(it.value)}.flatten()
        repeat(group.repeatCnt) {
            for (k in intData.indices) {
                val ipol = if(k==0) IpolType.HOLD else ipolType
                this.data.add(ParamInt(intData[k], ipol))
            }
        }
    }


    fun determineError(s : String) : ErrorMessage{
        val parentCommandRegex = Regex("(in)|(tk)|(tp)|(ss)")
        if(this.data.isEmpty() && isRootCommand(s)){
        //if(this.data.isEmpty() && s.matches(Regex("(\\s*)|(${parentCommandRegex.pattern}(\\.${r.regexVariable})?)"))) {
            return ErrorMessage.MISSING_DATA()
        }
        else{
            return classifySyntaxError(s)
        }

    }

    fun classifySyntaxError(s : String): ErrorMessage {
        val validsRemoved = s.replace(Regex("[\\-0-9\\[\\]]"),"")
        if(validsRemoved.isNotBlank()){
            return ErrorMessage.INVALID_CHARACTERS(validsRemoved)
        }
        if(s.matches(Regex(".*(\\^|\\*)\\d*\\D*\\d*"))){
            return ErrorMessage.NOTE_BAD_EXPONENT()
        }
        if(s.length==1 && !s.matches(Regex("\\s"))){
            return ErrorMessage.NOTE_SINGLE_DIGIT_NOT_ZERO()
        }
        if(s.matches(Regex("(\\-)?\\d\\d\\d\\d*"))){
            return ErrorMessage.INT_TOO_LONG()
        }
        if("-" in s){
            return ErrorMessage.INT_DASH_ERROR()
        }
        return ErrorMessage.SYNTAX_ERROR()
    }

}