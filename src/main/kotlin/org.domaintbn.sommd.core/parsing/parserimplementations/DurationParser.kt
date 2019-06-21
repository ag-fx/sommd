package org.domaintbn.sommd.core.parsing.parserimplementations


import org.domaintbn.sommd.core.musical.Duration
import org.domaintbn.sommd.core.musical.DurationCycle
import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*

import org.domaintbn.sommd.core.parsing.RegexRepo as r

class DurationParser : IParser {

    override val parserSignature: String = "Duration"


    private var durationCycle = DurationCycle()

    private fun isRootCommand(str : String) : Boolean{
        return RootCommandValidator.isValidRootCommand(str)
    }


    private fun parseFraction(s: String): Duration {
        return when {
            s == "0" -> {
                Duration(0, 1, true)
            }
            s.startsWith("s") -> {
                val s2 = s.drop(1).split("/")
                Pair(s2[0].toInt(), s2[1].toInt())
                Duration(s2[0].toInt(), s2[1].toInt(), true)
            }
            else -> {
                val s2 = s.split("/")
                Duration(s2[0].toInt(), s2[1].toInt(), false)
            }
        }
    }

    private fun parseAnyFraction(s : String) : List<Duration>{
        val repeatCnt : Int
        val text : String
        val out = mutableListOf<Duration>()
        if ("^" in s) {
            val splitString = s.split("^")
            text = splitString[0]
            repeatCnt = splitString[1].toInt()
        }else{
            text = s
            repeatCnt = 1
        }
        repeat(repeatCnt) {
            out.add(parseFraction(text))
        }
        return out
    }


    private fun buildDurationCommand(): Command {
        return object : Command {
            val durationCycle = this@DurationParser.durationCycle
            override fun applyOn(pl: Playhead): List<TimelineNote> {
                pl.loadDuration(durationCycle)
                return emptyList()
            }

            override val commandType = CommandType.REGULAR

        }
    }

    override fun getCommandSequence(): CommandSeq {
        if (this.durationCycle.isEmpty()) {
            error("Meh")
        }

        return CommandSeq(listOf(buildDurationCommand()))
    }

    override fun start(lex: Lexer) {
        var keepGoing = true
        while (lex.hasTokensLeft() && keepGoing) {
            lex.customSeparator = r.separatorToEnterGroups
            val ss = lex.peekNextToken()
            val s = ss.string
            when {

                s == "//" -> {
                    lex.consumeNextToken(this)
                    SingleCommentParser().apply { start(lex) }

                }


                s == "/*" -> {
                    lex.consumeNextToken(this)
                    MultiCommentParser().apply { start(lex) }
                }

                ss.isWhiteSpace() -> {
                    lex.consumeNextToken(this)

                }
                s == "[" ->{
                    lex.consumeNextToken(consumer= this)
                    val group =
                        BracketGroupParser(
                            true,
                            listOf(r.allFractionRx, r.repeatedFraction),
                            r.separatorToLeaveGroups,
                                this
                        )
                    try {
                        group.start(lex)
                    } catch (bpe : BracketParserException){
                        processBadGroupToken(bpe)
                    }
                    processGroup(false, group.recursiveStringList)
                }
                s == "-[" ->{
                    lex.consumeNextToken(consumer= this)
                    val group =
                        BracketGroupParser(
                            true,
                            listOf(r.allFractionRx, r.repeatedFraction),
                            r.separatorToLeaveGroups,
                                this
                        )
                    try {
                        group.start(lex)
                    } catch (bpe : BracketParserException){
                        processBadGroupToken(bpe)
                    }
                    processGroup(true, group.recursiveStringList)
                }
                s.matches(r.allFractionRx) -> {
                    lex.consumeNextToken(this)
                    val dur = parseFraction(s)
                    durationCycle.append(dur)
                }
                s.matches(r.stackedFractionRx) -> {
                    lex.consumeNextToken(this)
                    durationCycle.appendStacked2(s.split("-").map { parseFraction(it) })
                }
                s.matches(r.repeatedFraction) ->{
                    val x = s.split("^")
                    repeat(x[1].toInt()) {
                        durationCycle.append(parseFraction(x[0]))
                    }
                    lex.consumeNextToken(this)
                }
                isRootCommand(s) && !this.durationCycle.isEmpty() ->{
                    return
                }
                else -> {
                    keepGoing = false
                }
            }
        }

        if(!lex.hasTokensLeft() && !this.durationCycle.isEmpty()){
            return
        }


        val problemToken = lex.getLastFetchedToken()

        throw ParserException(problemToken,determineError(problemToken.string),this)
    }

    private fun processBadGroupToken(bpe: BracketParserException) {
        if(bpe.problemToken.string.matches(r.stackedFractionRx)){
            throw ParserException(bpe.problemToken,ErrorMessage.DURATION_GROUP_INVALID_TOKEN_STACKED(),bpe.source)
        }
    }

    private fun processGroup(shouldStack: Boolean, group: RecursiveList<String>) {
        val durations = group.content.map { parseAnyFraction(it.value) }
        if (shouldStack) {
            repeat(group.repeatCnt) {
                this.durationCycle.appendStacked2(durations.flatten())
            }
        } else {
            repeat(group.repeatCnt) {
                for (dur in durations.flatten()) {
                    this.durationCycle.append(dur)
                }
            }
        }

    }


    fun determineError(s : String) : ErrorMessage{
        if(this.durationCycle.isEmpty() && isRootCommand(s)){

//        if(this.durationCycle.isEmpty() && s.matches(Regex("(\\s*)|(dr(\\.${r.regexVariable})?)"))) {
            return ErrorMessage.MISSING_DATA()
        }
        else{
            return classifySyntaxError(s)
        }

    }

    fun classifySyntaxError(s : String): ErrorMessage {
        val validsRemoved = s.replace(Regex("[0-9\\^\\-\\/s]"),"")
        if(validsRemoved.isNotBlank()){
            return ErrorMessage.INVALID_CHARACTERS(validsRemoved)
        }
        if(s.matches(Regex("\\d*"))){
            return ErrorMessage.DURATION_LACKING_SLASH()
        }
        if(s.matches(Regex(".*(\\^|\\*)\\d*\\D\\d*"))){
            return ErrorMessage.BAD_EXPONENT()
        }
        if(s.length==1 && !s.matches(Regex("\\s"))){
            return ErrorMessage.DURATION_SINGLE_DIGIT_NOT_ZERO()
        }
        if("/" in s){
            return ErrorMessage.DURATION_WRONG_SLASH()
        }
        if("s" in s){
            return ErrorMessage.DURATION_WRONG_SILENCING()
        }
        return ErrorMessage.SYNTAX_ERROR()
    }

}



