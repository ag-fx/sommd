package org.domaintbn.sommd.core.musical


class TimelineNote(
    val p: PitchAbs,
    val location: MusicTime,
    val duration: MusicTime,
    val velocity: ParamPercentUni,
    val instrument: ParamInt,
    val track: ParamInt,
    val pan: ParamPercentBi,
    val paramX : ParamPercentUni,
    val paramY : ParamPercentUni

) {
    constructor(p: PitchAbs, loc: MusicTime, dur: MusicTime) : this(
        p, loc, dur,
        ParamPercentUni(0.8), ParamInt(0), ParamInt(0), ParamPercentBi
            ((0.0)), ParamPercentUni(0.5),ParamPercentUni(0.5)
    )


    fun getOnOffTime(): Pair<MusicTime, MusicTime> {
        return Pair(location, location + duration)
    }


    fun printMe(): String {
        return "${p.b12Value}, $location, $duration, v$velocity, i$instrument, p$pan"
    }

    fun printHeader(): String {
        return "pitch, start time, duration time, velocity, instrument, pan"
    }

}