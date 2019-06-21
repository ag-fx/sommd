package org.domaintbn.sommd.core.parsing



class ErrorMessage private constructor(    val description : String,
                       val cause : String,
                       val suggestion : String){


    companion object {


        fun BRANCH_ENDED_NON_EXISTING(): ErrorMessage {
            val description = "Branching issue"
            val cause = "Tried to end branch while not inside a branch."
            val suggestion = "Add a {${CommandStrings.BRANCH_START.txt}} command, or remove the " +
                    "{${CommandStrings.BRANCH_END.txt}} if it is not necessary."
            return ErrorMessage(description,cause,suggestion)
        }
        fun BRANCH_RESET_NON_EXISTING(): ErrorMessage {
            val description = "Branching issue"
            val cause = "Cannot reset a branch while not inside a one."
            val suggestion = "Add a {${CommandStrings.BRANCH_START.txt}} start command in front" +
                 "and a {${CommandStrings.BRANCH_END.txt}} after, or remove the" +
                    "{${CommandStrings.BRANCH_RESET.txt}} reset if it is not necessary."
            return ErrorMessage(description,cause,suggestion)
        }
        fun BRANCH_NOT_CLOSED(): ErrorMessage {
            val description = "Branching issue"
            val cause = "Current branch is not closed. Lacking branch end command."
            val suggestion = "Add a {${CommandStrings.BRANCH_END.txt}} end command to close the current branch."

            return ErrorMessage(description, cause, suggestion)
        }

        fun GROUP_NOT_CLOSED(): ErrorMessage {
            val description = "Grouped data issue"
            val cause = "Current group is not closed. Lacking {]}."
            val suggestion = "Add a {]} to close the current group"

            return ErrorMessage(description, cause, suggestion)
        }

        fun INVALID_GROUP_NESTING(): ErrorMessage {
            val description = "Grouped data issue"
            val cause = "Putting groups inside of groups are not supported."
            val suggestion = "Remove the inner group"

            return ErrorMessage(description, cause, suggestion)
        }

        fun GROUP_EMPTY(): ErrorMessage {
            val description = "Grouped data issue"
            val cause = "Group was closed/finalized without there being any data in it."
            val suggestion = "Add valid data inside the group. Usually some form of number"

            return ErrorMessage(description, cause, suggestion)
        }

        fun NOTE_SINGLE_DIGIT_NOT_ZERO(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Single digit data entry that was not 0"
            val suggestion = "Change this entry to 0 for a silent note, or make it two digits"

            return ErrorMessage(description, cause, suggestion)
        }

        fun NOTE_BAD_EXPONENT(): ErrorMessage {
            val description = "Exponent error"
            val cause = "Exponent (repetition {^} or extension {*}) was not of a valid form"
            val suggestion = """Expected form:
                    |{50^1}, {50*3}, , ... , {50^999}
                """.trimMargin()

            return ErrorMessage(description, cause, suggestion)
        }

        fun INVALID_CHARACTERS(invalidCharacters : String): ErrorMessage {
            val description = "Syntax error"
            val cause = "Found characters ($invalidCharacters) that are unsupported."
            val suggestion = """Remove the unsupported characters from the command, or fully type the next command
                """.trimMargin()

            return ErrorMessage(description, cause, suggestion)
        }

        fun VARIABLE_NOT_DEFINED(variableName : String): ErrorMessage {
            val description = "Variable usage problem"
            val cause = "Could not recognize variable with name $variableName"
            val suggestion = """Need a variable definition {$variableName= {..data}} to have come
                    |before the current command.
                """.trimMargin()

            return ErrorMessage(description, cause, suggestion)
        }

        fun UNSUPPORTED_VARIABLE_NAME(variableName : String): ErrorMessage {
            val description = "Variable usage problem"
            val cause = "The following variable name was not supported: $variableName"
            val suggestion = """Variable names must start with a letter, and otherwise only contain
                    |letters, numbers and underscores.
                """.trimMargin()

            return ErrorMessage(description, cause, suggestion)
        }

        fun NOTE_FIRST_DIGIT_NOT_NUMBER(): ErrorMessage {
            val description= "Syntax error"
            val cause = "First digit was not a number between 0-9"
            val suggestion = "Change first digit to a number between 0 and 9"

            return ErrorMessage(description, cause, suggestion)
        }

        fun NOTE_INVALID_NEGATIVE_INDEX(): ErrorMessage {
            val description= "Syntax error"
            val cause = "! was not placed correctly. (Not in front of a pair of digits)"
            val suggestion = "Negatively indexed pitches expected form: {!4a}, {[40 !43]}, {!38^2}"

            return ErrorMessage(description, cause, suggestion)
        }

        fun NOTE_TOO_MANY_DIGITS(): ErrorMessage {
            val description= "Syntax error"
            val cause = "Too many digits making up the pitch. Only need one for the octave and the second one for the index"
            val suggestion = "Pitch expected form: {50}, {45}, {4a}, {!50*4}"

            return ErrorMessage(description, cause, suggestion)
        }

        fun DURATION_WRONG_SLASH(): ErrorMessage {
            val description= "Syntax error"
            val cause = "Wrong usage of the / character in a fraction"
            val suggestion = "Fraction expected form: {1/3}, {4/1}, {s6/2}, {42/3^2}"

            return ErrorMessage(description, cause, suggestion)
        }

        fun DURATION_WRONG_SILENCING(): ErrorMessage {
            val description = "Syntax error"
            val cause = "\\s cannot be put anywhere but in front of the numerator"
            val suggestion = "Silenced fractions expected form: {s1/4}, {[s3/4]} {1/8-s7/8}"

            return ErrorMessage(description, cause, suggestion)
        }

        fun BAD_EXPONENT(): ErrorMessage {
            val description = "Exponent error"
            val cause = "Exponent (used for repetition {^}) was not of a valid form"
            val suggestion = """Exponents expected form:
                    |{..^1} ,{[.. ..^2 ..]^2}, {..^54}, , ... , {..^999}
                """.trimMargin()

            return ErrorMessage(description, cause, suggestion)
        }

        fun MISSING_DATA(): ErrorMessage {
            val description = "Missing data"
            val cause = "The current command finished without any data:"
            val suggestion = """Give the command some data, usually whitespace followed by a number like {0}, {2} or {40}
                """.trimMargin()

            return ErrorMessage(description, cause, suggestion)
        }

        fun INVALID_COMMAND(commandName : String): ErrorMessage {
            val description = "Unsupported command"
            val cause = "The following command was not supported: $commandName"
            val suggestion = """Available commands: ${CommandStrings.values().map{it.txt}}
                """.trimMargin()

            return ErrorMessage(description, cause, suggestion)
        }

        fun SCALE_NOT_GROUPED_PITCH(): ErrorMessage {
            val description = "Syntax error"
            val cause = "The scale command got a pitch that was not in a group or stacked"
            val suggestion = "Expected form: {40-42-43-47}, {[30 32 35 37 3a ]}"

            return ErrorMessage(description, cause, suggestion)
        }

        fun SYNTAX_ERROR(): ErrorMessage {
            val description = "Syntax error"
            val cause = "The data/command was not recognized"
            val suggestion = """Look in the examples to find valid syntax"""

            return ErrorMessage(description, cause, suggestion)
        }

        fun INT_DASH_ERROR() : ErrorMessage{
            val description = "Syntax error"
            val cause = "Dash/minus {-} was found at a wrong location"
            val suggestion = "Integer expected form: {0}, {1}, {-4}, {300}, {3}"
            return ErrorMessage(description, cause, suggestion)
        }

        fun INT_TOO_LONG() : ErrorMessage{
            val description = "Syntax error"
            val cause = "Too many digits. max 3 digits in integer data."
            val suggestion = "Integer expected form: {0}, {1}, {-4}, {300}, {3}"
            return ErrorMessage(description, cause, suggestion)
        }

        fun PERCENTAGE_UNI_DASHERROR() : ErrorMessage{
            val description = "Syntax error"
            val cause = "Dash/minus {-} is not supported for percentages"
            val suggestion = "Percentage expected form: {0}, {13}, {40.3}, {100^4}, {13.233}"
            return ErrorMessage(description, cause, suggestion)
        }

        fun DURATION_LACKING_SLASH(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Found number that was not a fraction. Lacking {/}"
            val suggestion = "Fraction data expected form: {1/3}, {4/1}, {s6/2}, {42/3^2}"
            return ErrorMessage(description, cause, suggestion)
        }

        fun DURATION_SINGLE_DIGIT_NOT_ZERO(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Single digit data entry that was not 0"
            val suggestion = "Change this entry to 0 for a zero-duration, or make it a fraction"

            return ErrorMessage(description,cause,suggestion)
        }

        fun PERCENTAGE_UNI_TOO_LONG(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Too many digits"
            val suggestion = "Either {100} or max two digits, though decimals are OK, e.g 50.43"

            return ErrorMessage(description,cause,suggestion)
        }

        fun SCALE_BAD_EXPONENT(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Incorrect usage of exponention for repetition {^}"
            val suggestion = "Expected form: {[40 47 49]^2}, {[50 52 53 55 57 58 5b]^3} , "
            return ErrorMessage(description,cause,suggestion)
        }

        fun SCALE_NEGATIVE_NOT_ALLOWED(): ErrorMessage {
            val description = "Syntax error"
            val cause = "On scale pitches {!} is not supported"
            val suggestion = "Expected form: {[40 42 43 45 47 48 4a]}, {[30 37 42]}"
            return ErrorMessage(description,cause,suggestion)
        }

        fun SCALE_STACKED_GROUP(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Stacking groups {-[ .. ] } are not supported by the scale command."
            val suggestion = "Expected form: {[40 42 43 45 47 48 4a]}, {[30 37 42]}"
            return ErrorMessage(description,cause,suggestion)
        }

        fun NOTE_GROUP_INVALID_TOKEN_STACKED(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Cannot use stacking with {-} inside bracket groups"
            val suggestion = "Example of advanced stacking: {[40 40, 0 50, 0 57]}"

            return ErrorMessage(description,cause,suggestion)
        }

        fun DURATION_GROUP_INVALID_TOKEN_STACKED(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Cannot use stacking with {-} inside bracket groups"
            val suggestion = "Example of stacking with groups: {1/2 -[1/4 s3/4]^2 1/4^2}"

            return ErrorMessage(description,cause,suggestion)
        }

        fun SCALE_GROUP_INVALID_TOKEN_STACKED(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Cannot use {-} inside scale command bracket groups"
            val suggestion = "Example of scale command bracket groups {[40 44 47]^3 [45 49 50]}"

            return ErrorMessage(description,cause,suggestion)
        }

        fun SCALE_SINGLE_DIGIT_NOT(): ErrorMessage {
            val description = "Syntax error"
            val cause = "Received pitch with only one digit. Two digits are required."
            val suggestion = "Example of valid pitch data for scale: {50} {43} {4b} "

            return ErrorMessage(description,cause,suggestion)
        }


    }
}