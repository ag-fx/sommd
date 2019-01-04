package org.domaintbn.sommd.core.musical

class ParamBool(override val value: Boolean, ipolType: IpolType) : IParam {
    constructor(value: Boolean) : this(value, IpolType.HOLD)

    override val ipolType = ipolType
    override val valType = PValType.BOOL

    init {
        valType.validate(value)
    }

    override fun copy(): ParamBool {
        return ParamBool(this.value, this.ipolType)
    }

    override fun toString(): String {
        return value.toString()
    }

}