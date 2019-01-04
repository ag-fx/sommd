package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.ParamScale
import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.Scale
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*

import org.domaintbn.sommd.core.parsing.RegexRepo as r

class ScaleParser() : IParser {

    override val parserSignature: String = "Scale"

    private val data = mutableListOf<ParamScale>()

    private fun isRootCommand(str : String) : Boolean{
        return RootCommandValidator.isValidRootCommand(str)
    }


    override fun start(lex: Lexer) {
        var keepGoing = true
        while (lex.hasTokensLeft() && keepGoing) {
            lex.customSeparator = r.separatorToEnterGroups
            val tok = lex.peekNextToken()
            when {
                tok.string == "//" -> {
                    lex.consumeNextToken(consumer =  this)
                    SingleCommentParser().apply { start(lex) }

                }
                tok.string == "/*" -> {
                    lex.consumeNextToken(consumer =  this)
                    MultiCommentParser().apply { start(lex) }
                }
                tok.isWhiteSpace() -> {
                    lex.consumeNextToken(consumer =  this)
                }
                tok.string.matches(r.pitchRegex) -> {
                    throw ParserException(
                        lex.getLastFetchedToken(),
                        ErrorMessage.SCALE_NOT_GROUPED_PITCH(),
                            this
                    )
                }
                tok.string == "[" ->{
                    lex.consumeNextToken(consumer= this)
                    val group = BracketGroupParser(
                        false,
                        listOf(r.pitchRegexB12),
                        r.separatorToLeaveGroups,
                            this
                    )
                    group.start(lex)
                    processGroup(group.recursiveStringList)
                }
                tok.string.matches(r.stackedPitchB12) -> {
                    lex.consumeNextToken(consumer =  this)
                    this.data.add(ParamScale(Scale(tok.string.split("-").map {
                        it.toInt(
                            12
                        )
                    })))
                }
                isRootCommand(tok.string) && this.data.isNotEmpty() ->{
                    return
                }
                else -> {
                    //return
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

    private fun buildScaleCommand(): Command {
        return object : Command {
            override val commandType = CommandType.REGULAR

            override fun applyOn(pl: Playhead): List<TimelineNote> {
                pl.loadScale(data.toList())
                return emptyList()
            }

        }
    }




    private fun processGroup(group : RecursiveList<String>){

        fun correctInt(s : String): Int {
            return 12*s[0].toInt()+
                    when(s[1]){
                        'a' -> 10
                        'b' -> 11
                        else -> s[1].toInt()
                    }
        }

        val intList = group.content.map{it.value.toInt(12)}

        data.add(ParamScale(Scale(intList)))
    }

    override fun getCommandSequence(): CommandSeq {
        return CommandSeq(listOf(buildScaleCommand()))
    }


    fun determineError(s : String) : ErrorMessage{
        if(this.data.isEmpty() && isRootCommand(s)) {

//        if(this.data.isEmpty() && s.matches(Regex("(\\s*)|(nt(\\.${r.regexVariable})?)"))) {
            return ErrorMessage.MISSING_DATA()
        }
        else{
            return classifySyntaxError(s)
        }

    }

    fun classifySyntaxError(s : String): ErrorMessage {
        val out = StringBuilder()
        val validsRemoved = s.replace(Regex("[0-9a-bA-B\\^\\-\\!\\[\\]]"),"")
        if(validsRemoved.isNotBlank()){
            return ErrorMessage.INVALID_CHARACTERS(validsRemoved)
        }
        if(s.matches(Regex(".*(\\^|\\*)\\d*\\D*\\d*"))){
            return ErrorMessage.SCALE_BAD_EXPONENT()
        }
        if(s.startsWith("-[")){
            return ErrorMessage.SCALE_STACKED_GROUP()
        }
        if(s.length==1 && !s.matches(Regex("\\s"))){
            return ErrorMessage.NOTE_SINGLE_DIGIT_NOT_ZERO()
        }
        if(s.matches(Regex("[0-9a-bA-b]{3,999}"))){
            return ErrorMessage.NOTE_TOO_MANY_DIGITS()
        }
        if(s.matches(Regex("[a-bA-B][0-9a-bA-B]"))){
            return ErrorMessage.NOTE_FIRST_DIGIT_NOT_NUMBER()
        }
        if("!" in s){
            return ErrorMessage.SCALE_NEGATIVE_NOT_ALLOWED()
        }
        return ErrorMessage.SYNTAX_ERROR()
    }

}