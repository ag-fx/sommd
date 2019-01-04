package org.domaintbn.sommd.core.musical

class Duration(val mt: MusicTime, val isSilent: Boolean = false) {
    constructor(num: Int, den: Int, isSilent: Boolean) : this(MusicTime(num, den), isSilent)
}