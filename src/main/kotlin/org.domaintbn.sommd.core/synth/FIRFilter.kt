package org.domaintbn.sommd.core.synth

import kotlin.math.pow


/**
 * Finite impulse response filter.

 */
class FIRFilter(val order: Int) : FilterInterface {
    //field
    private val filtercoeffs: DoubleArray
    private val filtercoeffsInterpolationTarget: DoubleArray
    private val inputbuffer: DoubleArray


    init {
        this.filtercoeffs = DoubleArray(order)
        this.filtercoeffsInterpolationTarget = DoubleArray(order)
        this.inputbuffer = DoubleArray(order)

        //standard filter is allpass
        this.filtercoeffs[order - 1] = 1.0

    }


    override fun applyFilter(input: Double): Double {
        return applyFilter(input, 0.0)
    }


    override fun applyFilter(input: Double, ipolPercentage: Double): Double {
        //scoot old values
        for (i in 0 until this.order - 1) {
            this.inputbuffer[i] = this.inputbuffer[i + 1]
        }
        this.inputbuffer[order - 1] = input

        var output = 0.0
        for (i in 0 until this.order) {
            val nextval =
                inputbuffer[i] * ((1 - ipolPercentage) * this.filtercoeffs[i] + ipolPercentage * this.filtercoeffsInterpolationTarget[i])
            output += nextval
        }

        return output
    }


    fun setInterpolationTarget(target: FIRFilter) {
        if (target.order == this.order) {
            for (i in 0 until order) {
                this.filtercoeffsInterpolationTarget[i] = target.filtercoeffs[i]
            }
        }
    }

    companion object {

        fun getMovingAverageFilter(order: Int): FIRFilter {
            val output = FIRFilter(order)
            for (i in 0 until order) {
                output.filtercoeffs[i] = 1.0 / order
            }
            return output
        }


        fun getStrangeFilter(order: Int): FIRFilter {
            val output = FIRFilter(order)
            for (i in 0 until order) {
                output.filtercoeffs[i] = (-1.0).pow(i.toDouble()) * ((0.0 + i) / order).pow(2.0)
            }
            return output
        }



        fun getAllPass(order: Int): FIRFilter {
            val output = FIRFilter(order)
            output.filtercoeffs[order - 1] = 1.0
            return output
        }
    }




}
