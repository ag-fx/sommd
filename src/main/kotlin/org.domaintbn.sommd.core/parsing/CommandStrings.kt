package org.domaintbn.sommd.core.parsing

enum class CommandStrings(val txt: String) {
    COMMENT_SINGLELINE("//"),
    COMMENT_MULTILINE("/*"),

    BRANCH_START("brs"),
    BRANCH_RESET("brr"),
    BRANCH_END("bre"),

    DURATION("dr"),
    INSTRUMENT("in"),
    NOTE("nt"),
    SCALE("sc"),
    SCALESHIFT("ss"),

    TRACK("tk"),
    TRANSPOSE("tp"),
    VELOCITY("ve"),

    PARAM_X_PARSER("px"),
    PARAM_Y_PARSER("py"),

    /*

    TODO : Add

    FORWARD("fw")
    STACCATO("st")
    CUSTOM_PARAMETER("pm") // for integer and percentageUni
            // pi["channel"] 4 3 1 6 7
            // pp["release"] 59 48 39

    when adding, remember the getCopy() and modulate() methods in Playhead class !!


     */


}