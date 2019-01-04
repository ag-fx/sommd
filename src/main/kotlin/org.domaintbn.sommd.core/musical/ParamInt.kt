package org.domaintbn.sommd.core.musical

class ParamInt(override val value: Int, ipolType: IpolType) : IParam {
    constructor(value: Int) : this(value, IpolType.HOLD)

    override val ipolType = ipolType
    override val valType = PValType.INT

    init {
        valType.validate(value)
    }

    override fun copy(): ParamInt {
        return ParamInt(this.value, this.ipolType)
    }

    override fun toString(): String {
        return value.toString()
    }


}