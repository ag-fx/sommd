package org.domaintbn.sommd.core.musical

class StepNote(val pitch: PitchRel, val start: Int, val steplen: Int, val repeatable: Boolean){

    constructor(pitch : PitchRel, start : Int) : this(pitch,start,1,true)

    constructor(pitch : PitchRel, start : Int, steplen: Int) : this(pitch,start,steplen,false)
}