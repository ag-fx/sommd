package org.domaintbn.sommd.core.musical

import kotlin.math.absoluteValue

class DurationCycle {


    private class DurationBlock(val mtStack: List<Duration>) {
        constructor(mt: MusicTime) : this(listOf(Duration(mt)))

        fun totalBlockTime(): MusicTime {
            var out = MusicTime()
            for (x in this.mtStack) {
                out += x.mt
            }
            return out
        }
    }

    private val stdDuration = MusicTime(0, 1, 4)
    private var pointer = 0

    private val durList = mutableListOf<DurationBlock>()


    fun clear() {
        this.durList.clear()
    }


    private fun append(num: Int, den: Int, isSilent: Boolean) {
        val bar: Int
        if (num > den) {
            bar = kotlin.math.floor(num.toDouble() / den.toDouble()).toInt()
            val mt = MusicTime(bar, num - bar * den, den)
            this.durList.add(DurationBlock(mt))
        } else {
            val mt = MusicTime(0, num, den)
            this.durList.add(DurationBlock(listOf(Duration(mt, isSilent))))
        }

    }

    fun getTime(steplen: Int): MusicTime {
        if (durList.isEmpty()) {
            return stdDuration * steplen
        }

        var out = MusicTime()
        for (k in 0..steplen - 1) {
            val tmp = durList[(pointer + k) % durList.size]
            val mt = tmp.totalBlockTime()
            out = out + mt
        }

        return out
    }

    fun getTimesStacked(steplen: Int): List<MusicTime> {
        if (durList.isEmpty()) {
            return listOf(stdDuration * steplen)
        }

        val out = mutableListOf<MusicTime>()
        for (k in 0..steplen - 1) {
            val tmp = durList[(pointer + k) % durList.size]
            out.addAll(tmp.mtStack.map { it.mt })
        }


        return out.toList()
    }


    fun getDurationsStacked(steplen: Int): List<Duration> {
        if (durList.isEmpty()) {
            return listOf(Duration(stdDuration * steplen))
        }

        val out = mutableListOf<Duration>()
        for (k in 0..steplen - 1) {
            val tmp = durList[(pointer + k) % durList.size]
            out.addAll(tmp.mtStack)
        }


        return out.toList()
    }




    fun shiftPointer(direction: Int) {
        if (durList.isEmpty()) return
        pointer = (pointer + direction) % durList.size
    }

    fun resetPointer() {
        this.pointer = 0
    }


    fun totalTime(): MusicTime {
        if (this.durList.isEmpty()) {
            return MusicTime(stdDuration.bar, stdDuration.frac)
        }
        var out = MusicTime()
        for (k in this.durList) {
            for (k2 in k.mtStack) {
                out = out + k2.mt
            }
        }
        return out
    }

    /**
     * Divides time into different bins, where each bin has width determined
     * by this duration cycle.
     *
     * steps: amount of division bins to create from the duration cycle
     */
    fun timeToCycleIndex(steps: Int, mt: MusicTime): Int {
        if (durList.isEmpty()) {
            return (mt.toDouble()/stdDuration.toDouble()).toInt() % steps
        }

        val mtFit = wrappedTime(steps, mt)

        val totBins = SimpleIntegerMath.lcm(this.durList.size, steps)

        var accTime = MusicTime()
        for (k in 0..(totBins - 1)) {
            accTime = accTime + this.durList[k % durList.size].totalBlockTime()
            //val accTimeNext = accTime + this.durList[(k + 1) % durList.size].totalBlockTime()
            if (accTime > mtFit) {

                return (k % steps)
            }
        }
        val zeroMt = MusicTime()
        if(accTime.compareTo(zeroMt)==0){
            return 0 //fix for this bug: dr 0 in 3 dr 1/4 nt 40 // that used to cause error
        }
        error("Should be unreachable")
    }

    /**
     * To find the length of the "time bin" for this given time
     */
    fun timeToCyclePosition(steps: Int, mt: MusicTime) : Double{
        if (durList.isEmpty()) {
            return (mt.toDouble()/stdDuration.toDouble()) % 1.0
        }

        val mtFit = wrappedTime(steps, mt)

        val totBins = SimpleIntegerMath.lcm(this.durList.size, steps)

        var accTime = MusicTime()
        for (k in 0..(totBins - 1)) {
            val toAddTime = this.durList[k % durList.size].totalBlockTime()

            //val accTimeNext = accTime + this.durList[(k + 1) % durList.size].totalBlockTime()
            if (accTime+toAddTime > mtFit) {
                return (mtFit.toDouble()-accTime.toDouble())/toAddTime.toDouble()
            }
            accTime = accTime + toAddTime
        }
        error("Should be unreachable")
    }


    /**
     * Finds the periodically equivalent of mt within this duration cycle
     *
     * steps: amount of division bins to create from the duration cycle
     */
    fun wrappedTime(steps: Int, mt: MusicTime): MusicTime {
        if (durList.isEmpty()) {
            error("Meh")
        }

        val totBins = SimpleIntegerMath.lcm(this.durList.size, steps)

        val totCycleTime = this.getTime(totBins)

        val tmp = mt.toDouble() / totCycleTime.toDouble()
        val cyclesPrior = kotlin.math.floor(tmp).toInt()

        val mtFit: MusicTime // the equivalent of mt that is ensured to be between 0 and total cycle time
        if (cyclesPrior.absoluteValue >= 1) {
            val toSubtract = totCycleTime * cyclesPrior
            mtFit = mt - toSubtract
        } else {
            mtFit = mt
        }
        return mtFit
    }

    /**
     * Finds the periodically equivalent of mt within this duration cycle,
     * and then checks the distance in time to the previous "time bin"
     *
     * steps: amount of division bins to create from the duration cycle
     */
    fun wrappedTimeBin(steps: Int, mt: MusicTime): MusicTime {
        if (durList.isEmpty()) {
            error("meh")
        }

        val mtFit = wrappedTime(steps, mt)

        val totBins = SimpleIntegerMath.lcm(this.durList.size, steps)

        var accTime = MusicTime()
        for (k in 0..(totBins - 1)) {
            val prevAccTime = MusicTime(accTime)
            accTime = accTime + this.durList[k % durList.size].totalBlockTime()

            if (accTime > mtFit) {

                return mtFit - prevAccTime
            }
        }
        error("Should be unreachable")
    }


    /**
     *
     */
    fun interPolationData(steps: Int, mt: MusicTime): Pair<Double, Int> {
        val wrapMt = wrappedTime(steps, mt)
        val kOut = timeToCycleIndex(steps, mt)
        val percentOut = (wrapMt.toDouble() / (this.durList[kOut].totalBlockTime().toDouble()))

        //validate
        PValType.PERCENTUNI.validate(percentOut)

        return Pair(percentOut, kOut)
    }


    fun appendStacked(numdenList: List<Int>) {
        if (numdenList.size % 2 != 0) {
            error("Need even length list")
        }
        val mtlist = mutableListOf<MusicTime>()
        for (k in numdenList.indices.filter { it % 2 == 0 }) {
            val num = numdenList[k]
            val den = numdenList[k + 1]

            mtlist.add(MusicTime(num, den))
        }
        this.durList.add(DurationBlock(mtlist.map { Duration(it) }))
    }

    fun appendStacked2(durations: List<Duration>) {
        this.durList.add(DurationBlock(durations))
    }


    fun appendSilent(num: Int, den: Int) {
        this.append(num, den, true)
    }

    fun append(num: Int, den: Int) {
        this.append(num, den, false)
    }

    fun append(dur: Duration) {
        this.durList.add(DurationBlock(listOf(dur)))
    }

    fun getCopy(): DurationCycle {
        val out = DurationCycle()
        out.durList.addAll(this.durList.toList())
        out.pointer = this.pointer
        return out
    }

    fun isEmpty(): Boolean {
        return this.durList.isEmpty()
    }

}
