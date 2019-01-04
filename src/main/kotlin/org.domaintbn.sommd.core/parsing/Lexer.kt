package org.domaintbn.sommd.core.parsing

import org.domaintbn.sommd.core.parsing.RegexRepo as r


class Lexer(inputText : String, private val onNextCallback: () -> Unit = {}){

    val text = inputText
    var ag = 0
    var bg = 0


        //val segment = Regex("(${r.notWhitespace}+${r.whitespace})|(${r.whitespace}+${r.notWhitespace})")
    val segment = Regex("(\\s+\\S)|(\\S+\\s)")

    val standardSeparators = Regex("(\\/\\/)|(\\/\\*)|(\\*\\/)|(\\,)|(\\[)|(\\])")


    var customSeparator : Regex? = null

    val currentSeparator : Regex get(){
        return customSeparator ?: standardSeparators
    }

    var peekCount = 0

    val currentBuf : String get(){
        return text.substring(ag..bg)
    }

    val currentSub : SubString get(){
        return SubString(text,ag..bg-1)
    }

    fun consumeNextToken( consumer : IParser) : SubString{

        onNextCallback()
        if(ag==bg){
            moveToNextToken()
        }
        // TODO store consumer
        val out = currentSub
        ag =bg
        peekCount = 0

        return out
    }

    fun moveToNextToken(){
        if(ag!=bg) error("Not allowed.")
        //while(hasTokensLeft() && !value.substring(ag..bg).matches(segment)){
        while(bg<text.length && !currentBuf.matches(segment)){


            bg++
        }
        bg--
        val separatorMatch = currentSeparator.find(currentBuf)

        if(separatorMatch!=null){
                val matchtxt = separatorMatch.value
            val nextIsSeparator = separatorMatch.range.start==0
            if(nextIsSeparator) {
                bg = ag + separatorMatch.range.start + matchtxt.length
            } else{
                bg = ag + separatorMatch.range.start
            }
        }else {
            bg++
        }

        lastFetchedToken = currentSub
    }

    fun peekNextToken() : SubString{

        peekCount++
        if(peekCount>500) error("Too many calls")
        if(ag==bg) moveToNextToken()
        val out = SubString(text,ag..bg-1)

        return out
    }

    private var lastFetchedToken: SubString? = null

    fun getLastFetchedToken(): SubString {

        val x = lastFetchedToken
        if (x == null) error("meh")

        val out = SubString(text, x.range)
        return out
    }

    fun getLineNumberOfLastFetchedToken() : Int{
        var acc = 0
        for(x in 0..ag){
            if(text[x] == '\n') acc++
        }
        return acc
    }

    fun hasTokensLeft() : Boolean{
        return ag<text.length
    }
}