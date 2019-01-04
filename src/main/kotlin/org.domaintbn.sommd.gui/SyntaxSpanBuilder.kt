package org.domaintbn.sommd.gui

import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import tornadofx.Controller
import java.lang.StringBuilder
import java.util.*

class SyntaxSpanBuilder : Controller(){

    private val r = object {
        //escaped literals
        private val eCommentMlStart = Regex.escape("/*")
        private val eCommentMlEnd = Regex.escape("*/")
        private val eSlash = Regex.escape("/")
        private val ePeriod = Regex.escape(".")
        private val eAsterix = Regex.escape("*")


        private val rWhitespace = Regex("[ \n\t(\r\n)]")

        private val rAlphaNumeric = Regex("[A-Za-z][A-Za-z0-9_]*")

        private val rVariableName = Regex("$ePeriod$rAlphaNumeric")


        private val rVariableNameDefinition = Regex("$rVariableName=$rWhitespace")
        private val rVariableNameLoad = Regex("$rVariableName(\\^\\d+)?$rWhitespace")

        private val rCommandBase = Regex("(nt)|(dr)|(in)|(tk)|(ve)|(sc)|(ss)|(tp)|(px)|(py)")

        private val rBranchNotStart = Regex("((brr)|(bre))$rWhitespace")
        private val rBranchStart = Regex("(brs)($rWhitespace|$rVariableNameDefinition)")


        private val rCommentSL = Regex("//[^\n]*\n?")
        private val rCommentML = Regex("($eCommentMlStart)([^($eCommentMlEnd)]|[$eSlash\\(\\)]|($eAsterix[^\\/]))*($eCommentMlEnd)?")

        val regexComment = Regex("($rCommentSL)|($rCommentML)")

        val regexCommand = Regex("($rCommandBase)(($rWhitespace)|($rVariableNameDefinition))")

        val regexBranch = Regex("($rBranchStart)|($rBranchNotStart)")

//
        val regexVariableDef = Regex("$rCommandBase")
        val regexVariableLoad = Regex("(($rCommandBase)|(brs))$rVariableNameLoad")

    }


    fun computeHighlight(originalText: String, callBack : () -> Unit): StyleSpans<Collection<String>>? {
        val spansBuilder = StyleSpansBuilder<Collection<String>>()



        val text = "$originalText " //append whitespace to make matching also work on the last character

        fun mergePatterns(rgs: List<Regex>): Regex {


            val pat = StringBuilder()

            for (x in rgs) {
                pat.append("(${x.pattern})|")
            }
            return Regex(pat.toString().dropLast(1))
        }

        val r0 = mergePatterns(listOf(r.regexComment, r.regexVariableLoad, r.regexCommand, r.regexBranch))


        var lastKwEnd = 0;

        var match = r0.find(text, lastKwEnd)
        while (match != null) {
            //val newKwEnd = match.groups.first()!!.range.start+match.value.length
            val newKwEnd = match.range.endInclusive




            val styleClass: String = when {
                match.value.matches(r.regexVariableLoad) -> "variable"
                match.value.matches(r.regexComment) -> "comment"
                match.value.matches(r.regexCommand) -> "command"
                match.value.matches(r.regexBranch) -> "branch"

                else -> "none"
            }



            callBack()

            spansBuilder.add(Collections.singleton("none"), (match.range.start) - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), match.range.endInclusive - match.range.start);
            lastKwEnd = newKwEnd
            match = r0.find(text, lastKwEnd)

        }


        spansBuilder.add(Collections.singleton("none"), originalText.length - lastKwEnd);
        return spansBuilder.create()
    }

}