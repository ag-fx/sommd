package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.PitchRel
import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.StepNoteBlock
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*
import org.domaintbn.sommd.core.parsing.RegexRepo as r



class NoteParser : IParser{


    override val parserSignature: String = "Note"




    private val stepNotes = StepNoteBlock()

    private fun isRootCommand(str : String) : Boolean{
        return RootCommandValidator.isValidRootCommand(str)
    }





    private fun parsePitch(text: String): PitchRel {
        return when {


            text == "0" -> {
                PitchRel()
            }
            text.startsWith("!") -> {
                val oct = text.substring(1, 2).toInt()
                val idx = (-1) * text.substring(2, 3).toInt(16)
                PitchRel(oct, idx)
            }
            else -> {
                val oct = text.substring(0, 1).toInt()
                val idx = text.substring(1, 2).toInt(16)
                PitchRel(oct, idx)
            }
        }
    }

    private fun parseStackedPitch(text: String): List<PitchRel> {
        return text.split("-").map { parsePitch(it) }
    }

    private fun parseAndAddRepeatedPitch(text : String) {
        val x = text.split("^")
        this.stepNotes.append(parsePitch(x[0]),steplen=x[1].toInt())
    }

    private fun parseAllPitch(text : String) : StepNoteBlock{
        val out = StepNoteBlock()

        when {
            "*" in text -> {
                val x = text.split("*")
                out.add(parsePitch(x[0]),x[1].toInt(),0)
                return out
            }
            "^" in text -> {
                val x = text.split("^")
                for(k in 1..x[1].toInt()){
                    out.append(parsePitch(x[0]),1)
                }
                return out
            }
            else -> {
                //if("^" !in text || "*" !in text){
                out.add(parsePitch(text), 1, 0)
                return out
                //}
            }
        }



    }


    private fun buildNoteCommand(): Command {
        return object : Command {
            val stepNoteBlock: StepNoteBlock = this@NoteParser.stepNotes
            override fun applyOn(pl: Playhead): List<TimelineNote> {
                return pl.play(stepNoteBlock)
            }

            override val commandType = CommandType.REGULAR

        }
    }


    override fun start(lex: Lexer) {
        var keepGoing = true

        while (lex.hasTokensLeft() && keepGoing) {
            lex.customSeparator = r.separatorToEnterGroups
            val ss = lex.peekNextToken()
            val token = ss.string
            when {

                token == "//" -> {
                    lex.consumeNextToken(consumer= this)
                    SingleCommentParser().apply { start(lex) }

                }


                token == "/*" -> {
                    lex.consumeNextToken(consumer= this)
                    MultiCommentParser().apply { start(lex) }
                }

                token == "[" ->{
                    lex.consumeNextToken(consumer= this)
                    val group = BracketGroupParser(
                        false,
                        //listOf(r.pitchRegex, r.pitchRepeatedRegex, Regex(",")),
                        listOf(r.pitchRegex, r.pitchRepeatedRegex, Regex(",")),
                        r.groupLeavingAndComma,
                            this
                    )
                    try {
                        group.start(lex)
                    } catch( bpe : BracketParserException){
                        processBadGroupToken(bpe)
                    }
                    processGroup(false,group.recursiveStringList)
                }

                token == "-[" ->{
                    lex.consumeNextToken( consumer = this)
                    val group =
                        BracketGroupParser(
                            true,
                            listOf(r.pitchRegex, r.pitchRepeatedRegex),
                            r.separatorToLeaveGroups,
                                this
                        )
                    try {
                        group.start(lex)
                    } catch( bpe : BracketParserException){
                        processBadGroupToken(bpe)
                    }
                    processGroup(true, group.recursiveStringList)
                }

                ss.isWhiteSpace() -> {
                    lex.consumeNextToken(consumer= this)
                }
                token.matches(r.pitchRegex) -> {
                    this.stepNotes.append(parsePitch(token))
                    lex.consumeNextToken(consumer= this)
                }

                token.matches(r.pitchRepeatedRegex) ->{
                    this.stepNotes.append(parseAllPitch(token))
                    lex.consumeNextToken(consumer= this)
                }

                token.matches(r.stackedPitch) -> {
                    this.stepNotes.appendStack(parseStackedPitch(token))
                    lex.consumeNextToken(consumer= this)
                }
                isRootCommand(token) && !this.stepNotes.isEmpty() ->{
                    return
                }
                else -> {
                    keepGoing = false
                }
            }
        }
        if(!lex.hasTokensLeft() && !this.stepNotes.isEmpty()){
            return
        }

        val problemToken = lex.getLastFetchedToken()

        throw ParserException(problemToken,determineError(problemToken.string),this)
    }

    private fun processBadGroupToken(bpe: BracketParserException){
        val token = bpe.problemToken
        if(token.string.matches(r.stackedPitch)){
            throw ParserException(token,ErrorMessage.NOTE_GROUP_INVALID_TOKEN_STACKED(),bpe.source)
        }
    }

    private fun processGroup(shouldStack : Boolean, group: RecursiveList<String>) {
        val out = StepNoteBlock()
        var stepPointer = -1

        fun processLocal(shouldStack: Boolean, group: RecursiveList<String>){
            if (group.isSingleton) {
                when {
//                    group.value.matches(r.stackedPitch) -> {
//                            throw ParserException(SubString("meh"), ErrorMessage.NOTE_GROUP_INVALID_TOKEN_STACKED(), this)
//                    }
                    group.value == "," -> {
                        stepPointer = -1
                    }
                    shouldStack ->
                        out.add(parseAllPitch(group.value), stepPointer)
                    else -> {
                        stepPointer += 1
                        val tmpBlock = parseAllPitch(group.value)
                        out.add(tmpBlock, stepPointer)
                        stepPointer +=tmpBlock.getTotalSteps()-1
                    }
                }

            } else {
                    for (k in group.content.indices) {
                        val doStack = if (k == 0) false else shouldStack
                        processLocal(doStack, group.content[k])
                    }
                //}

            }
        }
        processLocal(shouldStack, group)
        repeat(group.repeatCnt) {
            this.stepNotes.append(out)
        }
    }


    override fun getCommandSequence(): CommandSeq {
        if (this.stepNotes.isEmpty()) {
            error("Meh")
        }

        return CommandSeq(listOf(buildNoteCommand()))
    }



    fun determineError(s : String) : ErrorMessage{
        if(this.stepNotes.isEmpty() && isRootCommand(s)){

//        if(this.stepNotes.isEmpty() && s.matches(Regex("(\\s*)|(nt(\\.${r.regexVariable})?)"))) {
            return ErrorMessage.MISSING_DATA()
        }
            else{
            return classifySyntaxError(s)
        }

    }

    fun classifySyntaxError(s : String): ErrorMessage {
        val validsRemoved = s.replace(Regex("[0-9a-fA-F\\^\\-\\*\\!\\[\\]]"),"")
        if(validsRemoved.isNotBlank()){
            return ErrorMessage.INVALID_CHARACTERS(validsRemoved)
        }
        if(s.matches(Regex(".*(\\^|\\*)\\d*\\D*\\d*"))){
            return ErrorMessage.NOTE_BAD_EXPONENT()
        }
        if(s.length==1 && !s.matches(Regex("\\s"))){
            return ErrorMessage.NOTE_SINGLE_DIGIT_NOT_ZERO()
        }
        if(s.matches(Regex("[0-9a-fA-F]{3,999}"))){
            return ErrorMessage.NOTE_TOO_MANY_DIGITS()
        }
        if(s.matches(Regex("[a-fA-F][0-9a-fA-F]"))){
            return ErrorMessage.NOTE_FIRST_DIGIT_NOT_NUMBER()
        }
        if("!" in s){
           return ErrorMessage.NOTE_INVALID_NEGATIVE_INDEX()
        }
        return ErrorMessage.SYNTAX_ERROR()
    }



}