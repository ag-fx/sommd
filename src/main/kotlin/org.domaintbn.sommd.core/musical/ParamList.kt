package org.domaintbn.sommd.core.musical

class ParamList<T : IParam>(data: List<T>, val durCycle: DurationCycle, val refTime: MusicTime) :
    List<T> by ArrayList<T>(data) {
    constructor(data: T) : this(listOf(data), DurationCycle(), MusicTime())

    init {
        if (this.isEmpty()) error { "Must have at least one parameter" }
    }

    fun paramByTimeOld(mt: MusicTime): T {
        val idx = this.durCycle.timeToCycleIndex(this.size, mt - this.refTime)
        return this[idx]
    }

    fun paramByTime(mt: MusicTime): T {

        val idx = this.durCycle.timeToCycleIndex(this.size, mt - this.refTime)
        val nextIdx = (idx+1) % this.size
        val next = this[nextIdx]
        if(next.ipolType==IpolType.HOLD){
            return this[idx]
        }else{
            //linear
            val current = this[idx]
            val ipolPos = durCycle.timeToCyclePosition(this.size,mt-this.refTime)
            return current.interpolateWith(next,ipolPos) as T
        }

    }

    fun paramByTimeBoth(mt: MusicTime): Pair<T, T> {
        val idx = this.durCycle.timeToCycleIndex(this.size, mt - this.refTime)
        return Pair(this[idx], this[idx % this.size])
    }

    fun getCopy(): ParamList<T> {
        return ParamList(this.toList(), durCycle.getCopy(), MusicTime(refTime))
    }

}