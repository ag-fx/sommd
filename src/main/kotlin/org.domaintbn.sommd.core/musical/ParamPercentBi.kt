package org.domaintbn.sommd.core.musical

class ParamPercentBi(override val value: Double, ipolType: IpolType) :
    IParam {
    constructor(value: Double) : this(value, IpolType.HOLD)

    override val ipolType = ipolType
    override val valType = PValType.PERCENTBI

    init {
        if (!valType.validate(value)) error("wrong value!")
    }

    override fun copy(): ParamPercentBi {
        return ParamPercentBi(this.value, this.ipolType)
    }

    override fun toString(): String {
        return value.toString()
    }

}