package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.IpolType
import org.domaintbn.sommd.core.musical.ParamPercentUni
import org.domaintbn.sommd.core.parsing.*

import org.domaintbn.sommd.core.parsing.RegexRepo as r

class PercentageUniParser(parent : IParser) : IParser {

    override val parserSignature: String = parent.parserSignature+"/"+"Percentage"

    val data = mutableListOf<ParamPercentUni>()

    private fun isRootCommand(str : String) : Boolean{
        return RootCommandValidator.isValidRootCommand(str)
    }




    fun parsePercentageUni(s : String) : List<Double>{
        val out = mutableListOf<Double>()
        if("^" in s) {
            val x = s.split("^")
            repeat(x[1].toInt()) {
                out.add(x[0].toDouble()/100.0)
            }
        }else{
            out.add(s.toDouble()/100.0)
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
                tok.string.matches(r.regexPercentage) -> {
                    lex.consumeNextToken( consumer =this)
                    data.add(ParamPercentUni(tok.string.toDouble()/100.0))
                }
                tok.string.matches(r.regexPercentageRepeated) ->{
                    lex.consumeNextToken( consumer =this)
                    data.addAll(parsePercentageUni(tok.string).map{ParamPercentUni(it/100.0)})
                }
                tok.string == "[" ->{
                    lex.consumeNextToken(this)
                    val group =
                        BracketGroupParser(
                            true,
                            listOf(r.regexPercentage, r.regexPercentageRepeated),
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
                            listOf(r.regexPercentage, r.regexPercentageRepeated),
                            r.separatorToLeaveGroups,
                                this
                        )
                    try {
                        group.start(lex)
                    } catch (bpe : BracketParserException){
                        processBadGroupToken(bpe)
                    }
                    processGroup(IpolType.LINEAR,group.recursiveStringList)
                }
                isRootCommand(tok.string) && !this.data.isEmpty() ->{
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




    override fun getCommandSequence(): CommandSeq {
        error("Do not use!")
    }

    private fun processBadGroupToken(bpe: BracketParserException) {
        //currently no tokens accepted by Intparser that is not allowed
        //in sub bracket groups, so nothing to check
    }

    private fun processGroup(ipolType: IpolType, group : RecursiveList<String>){
        val percentUniData = group.content.map { parsePercentageUni(it.value) }.flatten()
        for(k in 0..percentUniData.size-1){
            val toAdd = when(k) {
                0 -> ParamPercentUni(percentUniData[k], IpolType.HOLD)
                else -> ParamPercentUni(percentUniData[k], ipolType)
            }
            this.data.add(toAdd)
        }
    }


    fun determineError(s : String) : ErrorMessage{
        val parentCommandRegex = Regex("(ve)|(px)|(py)")
        if(this.data.isEmpty() && isRootCommand(s)){

//        if(this.data.isEmpty() && s.matches(Regex("(\\s*)|(${parentCommandRegex.pattern}(\\.${r.regexVariable})?)"))) {
            return ErrorMessage.MISSING_DATA()
        }
        else{
            return classifySyntaxError(s)
        }

    }

    fun classifySyntaxError(s : String): ErrorMessage {
        val validsRemoved = s.replace(Regex("[0-9\\[\\]\\.]"),"")
        if(validsRemoved.isNotBlank()){
            return ErrorMessage.INVALID_CHARACTERS(validsRemoved)
        }
        if(s.matches(Regex(".*(\\^)\\d*\\D*\\d*"))){
            return ErrorMessage.BAD_EXPONENT()
        }
        if(s.matches(Regex("\\d{3,999}(\\..*)?"))){
            return ErrorMessage.PERCENTAGE_UNI_TOO_LONG()
        }
        if(s.length==1 && !s.matches(Regex("\\s"))){
            return ErrorMessage.NOTE_SINGLE_DIGIT_NOT_ZERO()
        }
        if("-" in s){
            return ErrorMessage.PERCENTAGE_UNI_DASHERROR()
        }
        return ErrorMessage.SYNTAX_ERROR()
    }

}