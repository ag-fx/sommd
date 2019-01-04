package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.parsing.*

class BracketGroupParser(val isStacking: Boolean, val validRegexList: List<Regex>, val lexerSeparators: Regex, parent : IParser) :
        IParser {

    override val parserSignature = parent.parserSignature+"/"+"BracketGroup"

    private val validTokensLocal = mutableListOf<String>()

    val validTokens: List<String> get() = validTokensLocal.toList()


    val recursiveStringList = RecursiveList<String>()


    override fun start(lex: Lexer) {
        var keepGoing = true
        var completed = false //flag to determine successful exit
        while (lex.hasTokensLeft() && keepGoing) {
            lex.customSeparator = lexerSeparators
            val token = lex.peekNextToken()
            when {
                token.string == "//" -> {
                    lex.consumeNextToken(consumer = this)
                    SingleCommentParser().apply { start(lex) }

                }


                token.string == "/*" -> {
                    lex.consumeNextToken(consumer = this)
                    MultiCommentParser().apply { start(lex) }
                }
                token.isWhiteSpace() -> {
                    lex.consumeNextToken(this)
                }
                validRegexList.any { it.matches(token.string) } -> {
                    //validTokensLocal.add(token.string)
                    recursiveStringList.append(RecursiveList(token.string))
                    lex.consumeNextToken(this)
                }
                token.string == "[" -> {
                    throw ParserException(token, ErrorMessage.INVALID_GROUP_NESTING(), this)

                }

                token.string.matches((RegexRepo.groupEndRepeatedParsing)) -> {
                    // ]^23 example input
                    lex.consumeNextToken(this)
                    if(recursiveStringList.isEmpty){
                        throw ParserException(lex.getLastFetchedToken(),ErrorMessage.GROUP_EMPTY(),this)
                    }
                    this.recursiveStringList.repeatCnt = token.string.split("^")[1].toInt()

                    keepGoing = false
                    completed = true
                }
                token.string == "]" -> {
                    lex.consumeNextToken(this)
                    if(recursiveStringList.isEmpty){
                        throw ParserException(lex.getLastFetchedToken(),ErrorMessage.GROUP_EMPTY(),this)
                    }
                    keepGoing = false
                    completed = true
                }

                else -> {
                    throw BracketParserException(lex.getLastFetchedToken(),this)
                    //keepGoing = false
                }
            }
        }

        if (!completed && keepGoing) {
            throw ParserException(lex.getLastFetchedToken(), ErrorMessage.GROUP_NOT_CLOSED(), this)
        }
        if(this.recursiveStringList.isEmpty && keepGoing){
            throw ParserException(lex.getLastFetchedToken(),ErrorMessage.GROUP_EMPTY(),this)
        }
    }

    override fun getCommandSequence(): CommandSeq {
        error("Do not use!")
    }


}