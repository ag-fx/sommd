package org.domaintbn.sommd.core.musical

//import org.apache.commons.math3.fraction.Fraction

/**
 * A class to keep track of time, that's better for comparisons than Double.
 * Does not lose precision with 1/7+1/7+1/7 .... etc
 *
 * Is simply a whole number together with a positive fraction < 1
 */
class MusicTime(bar: Int, frac: Fraction) : Comparable<MusicTime> {

    constructor(bar: Int, num: Int, den: Int) : this(bar, Fraction(num, den))

    constructor(num: Int, den: Int) : this(0, Fraction(num, den))

    constructor() : this(0, Fraction(0))

    constructor(other: MusicTime) : this(other.bar, other.frac)


    val bar: Int
    val frac: Fraction


    init {


        if (frac.toDouble() >= 1) {
            val divd = (frac.numerator / frac.denominator)
            this.bar = bar + divd
            this.frac = Fraction(frac.numerator - divd * frac.denominator, frac.denominator)
        } else {

            this.bar = bar
            this.frac = frac
        }

        when {
            this.bar < 0 -> error("Bar must be non-negative")
            this.frac.numerator >= this.frac.denominator -> error("Fraction must be <= 1")
            this.frac.toDouble() < 0 -> error("Fraction can't be negative")
        }

    }


    override fun compareTo(other: MusicTime): Int {
        return when {
            this.bar > other.bar -> 1
            this.bar == other.bar -> this.frac.compareTo(other.frac)
            this.bar < other.bar -> -1
            else -> error("Should've been unreachable")

        }
    }

    operator fun plus(other: MusicTime): MusicTime {
        var bar = this.bar + other.bar
        var fr = this.frac.add(other.frac)

        if (fr.numerator >= fr.denominator) {
            bar = bar + 1
            fr = Fraction(fr.numerator - fr.denominator, fr.denominator)
        }


        return MusicTime(bar, fr)
    }

    operator fun minus(other: MusicTime): MusicTime {
        var bar = this.bar - other.bar
        var fr = this.frac.subtract(other.frac)

        if (fr.toDouble() < 0) {
            bar = bar - 1
            fr = Fraction(fr.denominator + fr.numerator, fr.denominator)
        }

        return MusicTime(bar, fr)
    }


    override fun toString(): String {
        return "$bar+(${frac.numerator}/${frac.denominator})"
        //return "$bar+(${frac.toString()})"
    }

    fun toDouble(): Double {
        return bar.toDouble() + frac.toDouble()
    }


    operator fun times(other: Int): MusicTime {
        var fr = this.frac.multiply(other)
        val divd = fr.numerator / fr.denominator
        fr = Fraction(fr.numerator - (divd * fr.denominator), fr.denominator)

        return MusicTime(this.bar * other + divd, fr)
    }

    fun getCopy(): MusicTime {
        return MusicTime(this)
    }


}