package org.domaintbn.sommd.core.musical
interface IParam {
    val ipolType: IpolType
    val valType: PValType
    val value: Any

    fun copy(): IParam

    override fun toString(): String

    fun interpolateWith(other : IParam, ipolPos : Double): IParam {
        if (ipolPos !in 0.0..1.0) error("Parameter must be between 0 and 1. Was $ipolPos")
        if(other::class != this::class){
            error("Can only interpolate between the same kind of objects")
        }
        when (other) {
            is ParamPercentUni -> {
                val from = (this as ParamPercentUni).value
                val to = other.value
                val out = ParamPercentUni(from * (1-ipolPos) + to * (ipolPos))
                return out
            }
            is ParamInt -> {
                val from = (this as ParamInt).value
                val to = other.value
                val out = ParamInt((from*(1-ipolPos)+to*(ipolPos)).toInt())
                return out
            }
            else ->{
                //
                return this
            }
        }
    }


}