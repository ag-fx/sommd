package org.domaintbn.sommd.core.musical

class MIDINoteAbsTime(
    val absTime: Long
    , val pitch: Int
    , val OnNotOff: Boolean
    , val Velocity: Int = 100
    , val Channel: Int = 0
) {

}