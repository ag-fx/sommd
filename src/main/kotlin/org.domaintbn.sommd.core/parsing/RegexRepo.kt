package org.domaintbn.sommd.core.parsing

class RegexRepo{

    companion object {


        val repeatRegex = Regex("\\^[1-9][0-9]{0,2}")

        val pitchRegex = Regex("0|(!?[0-9][0-9a-fA-F])")
        val stackedPitch = Regex("!?[0-9][0-9a-fA-F](-!?[0-9][0-9a-fA-F])+")
        val pitchRepeatedRegex = Regex("(${pitchRegex.p})(\\^|\\*)[1-9]\\d{0,2}")

        val pitchRegexB12 = Regex("[0-9][0-9a-bA-B]")
        val stackedPitchB12 = Regex("(${pitchRegexB12.p})(\\-${pitchRegexB12.p})+")


        val fractionRx = Regex("0|[1-9][0-9]{0,3}/[1-9][0-9]{0,3}")
        val allFractionRx = Regex("s?(${fractionRx.p})")
        val stackedFractionRx = Regex("${allFractionRx.p}(-${allFractionRx.p})*")
        val repeatedFraction = Regex("(${fractionRx.p})${repeatRegex.p}")



        val regexVariable = Regex("[a-zA-Z][a-zA-Z0-9_]+((\\^\\d{1,3})|=)?")

        val regexInt = Regex("\\-?\\d{1,3}")
        val regexPositiveInt = Regex("\\d{1,3}")

        val repeatedInt = Regex("${regexInt.p}${repeatRegex.p}")
        val repeatedPositiveInt = Regex("${regexPositiveInt.p}${repeatRegex.p}")

        val regexPercentage = Regex("100|\\d\\d?(\\.\\d+)?")
        val regexPercentageRepeated = Regex("${regexPercentage.p}${repeatRegex.p}")

        val commentSep = Regex("(//)|(/\\*)")

        // for parsing. has restriction on number of digits
        val groupEndRepeatedParsing = Regex("\\]${repeatRegex.p}")

        val groupStartSep = Regex("(-\\[)|(\\[)")
        val groupEndSep = Regex("(\\]\\^\\d*)|(])") //for separation, don't care about amount of digits
        val separatorToEnterGroups = Regex("${commentSep.p}|${groupStartSep.p}") // TODO

        val separatorToLeaveGroups = Regex("${commentSep.p}|${groupEndSep.p}")

        val groupLeavingAndComma = Regex("${separatorToLeaveGroups.p}|,")


        val whitespace = Regex("[ (\\r\\n)\\n\\t]")
        val notWhitespace = Regex("[^ (\\r\\n)\\n\\t]")


        val Regex.p : String get(){
            return this.pattern
        }

    }
}