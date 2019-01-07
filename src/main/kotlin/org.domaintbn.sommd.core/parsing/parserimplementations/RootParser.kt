package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*
import org.domaintbn.sommd.core.parsing.RegexRepo as r

class RootParser
private constructor(variableHandler: VariableHandler, private var isInsideBranch: Boolean) : IParser {

    override val parserSignature = if(isInsideBranch) "Branch" else "Root"

    public constructor() : this(VariableHandler(), false)

    private val varHandler: VariableHandler = variableHandler


    private val subParserErrors = mutableListOf<ParserException>()

    /**
     * Class to handle when variables will be stored, fetched,
     * and when commands will be sent directly to the root sequence
     */
    private class VariableHandler {
        val varMap = mutableMapOf<String, List<Command>>()



    }

    private fun checkCommand(str: String): CommandStrings? {
        for (x in CommandStrings.values()) {
            if (x.txt == str) return x
        }
        return null
    }


    private fun stripRepeatFromVariable(str : String) : Pair<String,Int>{
        if("^" in str){
            val x = str.split("^")
            return Pair(x[0],x[1].toInt())
        }
        else{
            return Pair(str,1)
        }
    }

    private enum class CommandOperation {
        SKIP, APPLY, STOREVAR, LOADVAR, INVALID, DONE
    }

    private fun determineOperation(str: String): CommandOperation {
        fun isVariable(str: String): Boolean {
            val x = str.split(".")

            if (x.size != 2) return false


//            return (checkCommand(x[0]) != null
//                    && x[1].matches(r.regexVariable))
            return checkCommand(x[0]) != null
        }

        if (isVariable(str)) {
            if (str.endsWith("=")) {
                return CommandOperation.STOREVAR
            }
            return CommandOperation.LOADVAR
        }

        if (checkCommand(str) != null) {
            if (this.isInsideBranch &&
                checkCommand(str) == CommandStrings.BRANCH_END
            ) {
                return CommandOperation.DONE
            }
            return CommandOperation.APPLY
        }
        if (str.matches(Regex("\\s*"))) {
            return CommandOperation.SKIP
        }



        return CommandOperation.INVALID
    }



    val rootCommSeq = CommandSeq()




    override fun getCommandSequence(): CommandSeq {
        return rootCommSeq
    }


    override fun start(lex: Lexer) {
        var keepGoing = true
        while (lex.hasTokensLeft() && keepGoing) {
            lex.customSeparator = r.commentSep
            val ss = lex.peekNextToken()


            val nextOperation = determineOperation(ss.string)

            when (nextOperation) {

                CommandOperation.SKIP -> {
                    lex.consumeNextToken(consumer= this)
                }
                CommandOperation.APPLY -> {
                    lex.consumeNextToken(consumer= this)
                    this.rootCommSeq.addAll(startSub(lex))

                }
                CommandOperation.STOREVAR -> {
                    lex.consumeNextToken(consumer= this)
                    val compound = ss.string.split(".")
                    if(!compound[1].matches(r.regexVariable)){
                        throw ParserException(ss,ErrorMessage.UNSUPPORTED_VARIABLE_NAME(compound[1]),this)
                    }
                    val commName = compound[0]
                    varHandler.varMap.put(ss.string.dropLast(1), startSub(lex, commName))
                }
                CommandOperation.LOADVAR -> {
                    lex.consumeNextToken(consumer = this)

                    val compound = ss.string.split(".")

                    if(!compound[1].matches(r.regexVariable)){
                        throw ParserException(ss,ErrorMessage.UNSUPPORTED_VARIABLE_NAME(compound[1]),this)
                    }

                    val x = stripRepeatFromVariable(ss.string)
                    val retrievedVar = varHandler.varMap[x.first]

                    if (retrievedVar == null) {
                        throw ParserException(
                                lex.getLastFetchedToken(),
                                ErrorMessage.VARIABLE_NOT_DEFINED(ss.string),
                                this
                        )
                    }

                    repeat(x.second) {
                    this.rootCommSeq.addAll(retrievedVar)
                    }
                }
                CommandOperation.DONE -> {
                    lex.consumeNextToken(consumer= this)
                    this.rootCommSeq.add(buildBranchCommand(CommandType.BRANCH_END))
                    this.isInsideBranch = false
                    keepGoing = false
                }
                CommandOperation.INVALID -> {
                    val compoundErrors = StringBuilder()
                    throw ParserException(
                        lex.getLastFetchedToken(),
                        ErrorMessage.INVALID_COMMAND(ss.string),
                            this
                    )
                }

            }

        }
        if(this.isInsideBranch && !lex.hasTokensLeft()){
            throw ParserException(
                lex.getLastFetchedToken(),
                    ErrorMessage.BRANCH_NOT_CLOSED(),
                    this
            )
        }
    }


    private fun buildBranchCommand(branchType: CommandType): Command {
        when (branchType) {

            CommandType.BRANCH_START
                , CommandType.BRANCH_RESET
                , CommandType.BRANCH_END -> {
                val out = object : Command {
                    override val commandType = branchType
                    override fun applyOn(pl: Playhead): List<TimelineNote> {
                        error("Do not use")
                    }
                }
                return out
            }
            else -> {
                error("Told to create something that isn't a branch")
            }
        }
    }


    private fun startSub(lex: Lexer, str: String? = null): CommandSeq {
        val tok = str ?: lex.getLastFetchedToken().string

        fun subParse(parser: IParser): CommandSeq {

            //this.subParserErrors.clear()
            //this.subParserErrors.addAll(parser.getErrorMessages())
            parser.start(lex)
            return parser.getCommandSequence()
        }

        fun branchHandle(branchtype: CommandType): CommandSeq {
            when (branchtype) {
                CommandType.BRANCH_START -> {
                    val out = buildBranchCommand(branchtype)
                    val sub = RootParser(this.varHandler, true)
                    sub.start(lex)
                    val outList = mutableListOf<Command>(out)
                    outList.addAll(sub.rootCommSeq.toList())
                    return CommandSeq(outList)
                }
                CommandType.BRANCH_RESET -> {
                    if (this.isInsideBranch) {
                        val out = buildBranchCommand(branchtype)
                        return CommandSeq(listOf(out))
                    } else {
                        throw ParserException(
                            lex.getLastFetchedToken(),
                                ErrorMessage.BRANCH_RESET_NON_EXISTING(),
                                this
                            //"Not inside branch. Need preceding branch start"
                        )
                    }
                }
                CommandType.BRANCH_END -> {
                    if (this.isInsideBranch) {
                        val out = buildBranchCommand(branchtype)
                        return CommandSeq(listOf(out))
                    } else {
                        throw ParserException(
                            lex.getLastFetchedToken(),
                            //"Not inside branch. Need preceding branch start"
                                ErrorMessage.BRANCH_ENDED_NON_EXISTING(),
                                this
                        )
                    }
                }
                else -> {
                    error("Told to handle branch that is not a branch")
                }
            }

        }

        return when (checkCommand(tok)) {
            CommandStrings.NOTE -> {
                subParse(NoteParser())
            }
            CommandStrings.DURATION -> {
                subParse(DurationParser())
            }
            CommandStrings.SCALE -> {
                subParse(ScaleParser())
            }
            CommandStrings.SCALESHIFT -> {
                subParse(ScaleShiftParser())
            }
            CommandStrings.INSTRUMENT -> {
                subParse(InstrumentParser())
            }
            CommandStrings.VELOCITY -> {
                subParse(VelocityParser())
            }
            CommandStrings.COMMENT_SINGLELINE -> {
                subParse(SingleCommentParser())
            }
            CommandStrings.COMMENT_MULTILINE -> {
                subParse(MultiCommentParser())
            }
            CommandStrings.TRACK -> {
                subParse(TrackParser())
            }

            CommandStrings.TRANSPOSE -> {
                subParse(TransposeParser())
            }

            CommandStrings.BRANCH_START -> {
                branchHandle(CommandType.BRANCH_START)
            }
            CommandStrings.BRANCH_RESET -> {

                branchHandle(CommandType.BRANCH_RESET)
            }
            CommandStrings.BRANCH_END -> {
                branchHandle(CommandType.BRANCH_END)

            }
            CommandStrings.PARAM_X_PARSER -> {
                subParse(ParamXParser())
            }
            CommandStrings.PARAM_Y_PARSER -> {
                subParse(ParamYParser())
            }

            null -> error("Should never happen")


        }
    }

}