package org.domaintbn.sommd.gui

import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.ParserException

class CompilationResult(
    val outputNotes : List<TimelineNote>,
    val parserException: ParserException?,
    val aborted : Boolean = false
)
