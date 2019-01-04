package org.domaintbn.sommd.core.parsing

import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote


interface Command {
    val commandType: CommandType
    fun applyOn(pl: Playhead): List<TimelineNote>
}