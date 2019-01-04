package org.domaintbn.sommd.core.synth

/**
 * Filter interface to make different filters
 * easier to use.
 */
interface FilterInterface {

    /**
     *
     * @param input
     * @return
     */
    fun applyFilter(input: Double): Double

    /**
     *
     * @param input
     * @param ipolPercentage percentage of filter modification
     * @return
     */
    fun applyFilter(input: Double, ipolPercentage: Double): Double

}