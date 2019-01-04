package org.domaintbn.sommd.core.musical

class ParamPercentUni(override val value: Double, ipolType: IpolType) : IParam {
    constructor(value: Double) : this(value, IpolType.HOLD)

    override val ipolType = ipolType
    override val valType = PValType.PERCENTUNI

    init {
        if (!valType.validate(value)) error("Wrong value")

    }

    override fun copy(): ParamPercentUni {
        return ParamPercentUni(this.value, this.ipolType)
    }

    override fun toString(): String {
        return value.toString()
    }

}