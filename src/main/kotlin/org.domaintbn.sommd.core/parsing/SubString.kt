package org.domaintbn.sommd.core.parsing

class SubString(
    string: String, val range: IntRange
) {
    private val origString = string

    constructor(str : String) : this(str,0..str.length-1)

    override fun toString(): String {
        return "{$string} @[$range]"
    }

    fun toStringNoNewlines() : String{
        return "{${string.replace("\n"," ")}} @[$range]"
    }

    val string : String get(){
        return origString.substring(range)
    }

    fun isWhiteSpace(): Boolean {
        return this.string.matches(Regex("${RegexRepo.whitespace.pattern}+"))

    }
}



//class SubString(
//    val string: String, val range: IntRange
//) {
//
//    constructor(str : String) : this(str,0..str.length)
//    override fun toString(): String {
//        return "{$range} , {$string}"
//    }
//
//    fun isWhiteSpace(): Boolean {
//        return this.string.matches(Regex("[ (\r\n)\n\t]+"))
//
//    }
//}