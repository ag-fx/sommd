package org.domaintbn.sommd.core.musical


class ParamScale(override val value: Scale, ipolType: IpolType) :
    IParam {
    constructor(value: Scale) : this(value, IpolType.HOLD)

    constructor(data: List<Int>) : this(Scale(data))

    override val ipolType = ipolType
    override val valType = PValType.SCALE

    init {
        if (!valType.validate(value)) error("Wrong value!")
    }

    override fun copy(): ParamScale {
        return ParamScale(this.value, this.ipolType)
    }

    override fun toString(): String {
        return value.toString()
    }


}



