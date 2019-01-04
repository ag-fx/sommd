package org.domaintbn.sommd.core.parsing

class CommandSeq(data: List<Command>) : MutableList<Command> by ArrayList<Command>(data) {
    constructor() : this(listOf())


}