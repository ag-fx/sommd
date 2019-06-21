package org.domaintbn.sommd.core.musical

class Scale(private val pitches: List<Int>) {

    constructor() : this((12 * 5..(12 * 5 + 11)).toList())

    init {
        when {
            pitches.size !in 0..16 -> error("Meh")
            //pitches.any { it !in 0..16 } -> error("meh")
            else -> {
            }
        }
    }

    private fun applyScale(idx: Int): PitchAbs {
        val octOffset: Int = when {
            idx < 0 -> PitchRel.Companion.stdOctave - 1
            else -> PitchRel.Companion.stdOctave
        }

        val scaleSize = this.pitches.size

        val idxWrapped = when {
            idx < 0 -> (idx % scaleSize) + scaleSize
            else -> (idx % scaleSize)
        }

        return PitchAbs(this.pitches[idxWrapped] + octOffset * 12)
    }


    fun applyScale(pr: PitchRel, shift: Int, transpose : Int): PitchAbs {
        val shiftedPitch = PitchRel(pr.oct, pr.idx + shift)

        val out = applyScale(shiftedPitch)
        return PitchAbs(out.value+transpose)
    }


    fun applyScale(pr: PitchRel): PitchAbs {
        val idx = pr.idx
        val scaleSize = this.pitches.size



        val oct: Int = when {
            idx < 0 && (idx % scaleSize) == 0 ->
                (pr.oct - PitchRel.Companion.stdOctave) + (idx / scaleSize)
            idx < 0 ->
                (pr.oct - PitchRel.Companion.stdOctave) + (idx / scaleSize) - 1
            else ->
                (pr.oct - PitchRel.Companion.stdOctave) + (idx / scaleSize)
        }

        val idxWrapped = when {
            idx % scaleSize == 0 -> 0
            idx < 0 -> (idx % scaleSize) + scaleSize
            else -> (idx % scaleSize)
        }

        //return PitchAbs(this.pitches[idxWrapped] + (PitchRel.stdOctave + oct) * 12)
        return PitchAbs(this.pitches[idxWrapped] + oct * 12)
    }
}