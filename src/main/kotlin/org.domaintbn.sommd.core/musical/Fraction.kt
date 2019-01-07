package org.domaintbn.sommd.core.musical


class Fraction(num : Int, den: Int){

    constructor(num : Int) : this(num,1)

    val numerator : Int
    val denominator : Int

    init{
        if(den==0){
            error("Cannot have 0 in the denominator.")
        }
        if(den<0){
            error("Cannot have negative number in the denominator.")
        }
        val gcf = if(num!=0) {
            SimpleIntegerMath.gcf(num, den)
        }else{
            1
        }
        this.numerator = num/gcf
        this.denominator = den/gcf
    }

    fun toDouble() : Double{
        if(numerator==denominator) return 1.0
        return (numerator+0.0)/(denominator+0.0)
    }

    fun multiply(other: Int): Fraction {
        return Fraction(this.numerator*other,this.denominator)
    }


    fun subtract(other : Fraction) :Fraction {
        return add(Fraction(-other.numerator,other.denominator))
    }

    fun add(other: Fraction): Fraction {
        if(this.numerator==0){
            return other
        }else if(other.numerator==0){
            return this
        }
        val commonDen = SimpleIntegerMath.lcm(this.denominator,other.denominator)
        val thisScaling = commonDen/this.denominator
        val otherScaling = commonDen/other.denominator
        return Fraction(this.numerator*thisScaling+other.numerator*otherScaling,commonDen)
    }


    private fun addCommon(other: Fraction) : Fraction{
        if(this.denominator==other.denominator){
            return Fraction(this.numerator+other.numerator,this.denominator)
        }else{
            error("Invalid!!")
        }
    }


    fun compareTo(other: Fraction): Int {
        return this.toDouble().compareTo(other.toDouble())
    }

    override fun toString() : String{
        return "$numerator/$denominator"
    }

    fun divide(other : Fraction) : Fraction{
        if(other.numerator ==0){
            return Fraction(0,1) // TODO bugprone?
        }
        return Fraction(this.numerator*other.denominator,this.denominator*other.numerator)
    }
}