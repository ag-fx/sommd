package org.domaintbn.sommd.core.musical

import kotlin.math.absoluteValue

/**
 * Absolute pitch.
 */
data class PitchAbs(val value: Int) {

    private fun toB12(num : Int): String {
        val absNum = num.absoluteValue
        val out = (absNum/12).toString() + (absNum % 12).toString(12)
        return if(num<0) "-$out" else out
    }

    val b12Value = toB12(value)
    val isExportable = value in 0..127

}
