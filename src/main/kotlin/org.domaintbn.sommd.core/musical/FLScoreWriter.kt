package org.domaintbn.sommd.core.musical


import kotlin.math.min


object FLScoreWriter {


    //header for all fsc files
    private val header = intArrayOf(
        0x46,
        0x4C,
        0x68,
        0x64,
        0x06,
        0x00,
        0x00,
        0x00,
        0x10,
        0x00,
        0x01,
        0x00,
        0x60,
        0x00,
        0x46,
        0x4C,
        0x64,
        0x74
    )


    private val standardNote =
        intArrayOf(0, 0, 0, 0, 0, 64, 0, 0, 24, 0, 0, 0, 60, 0, 0, 0, 117, 0, 95, 0, 64, 100, 128, 128)

    private fun fileLengthInfo(databytes1: Int): IntArray {
        var databytes = databytes1
        val output = IntArray(4)
        //16^0

        var byte1 = 0
        var byte2 = 0
        var byte3 = 0
        val byte4 = 0

        if (databytes > 256 * 256) {
            byte3 = databytes / (256 * 256)
            databytes -= byte3
        }
        if (databytes > 256) {
            byte2 = databytes / 256
            databytes -= byte2
        }
        byte1 = databytes

        output[0] = byte1
        output[1] = byte2
        output[2] = byte3
        output[3] = byte4

        return output
    }


    private fun somestuff(noteCount: Int): IntArray {
        //0xC7, 0x06, 0x33, 0x2E, 0x35, 0x2E, 0x30, 0x00, 0x41, 0x00, 0x00, 0xE0, 0x78, 0x00

        val stdlow = intArrayOf(
            0xC7,
            0x09,
            0x31,
            0x32,
            0x2E,
            0x32,
            0x2E,
            0x30,
            0x2E,
            0x33,
            0x00,
            0x1C,
            0x03,
            0x41,
            0x00,
            0x00,
            0xE0,
            0x30
        )

        val stdhigh = intArrayOf(
            0xC7,
            0x09,
            0x31,
            0x32,
            0x2E,
            0x32,
            0x2E,
            0x30,
            0x2E,
            0x33,
            0x00,
            0x1C,
            0x03,
            0x41,
            0x00,
            0x00,
            0xE0,
            0x00,
            0x00
        )

        if (noteCount > 5) {
            stdhigh[stdhigh.size - 1] = 1
            stdhigh[stdhigh.size - 2] = 120

            for (i in noteCount downTo 6) {
                val previous = stdhigh[stdhigh.size - 2]
                if (previous + 24 > 255) {
                    stdhigh[stdhigh.size - 1]++
                    stdhigh[stdhigh.size - 2] = previous - 104

                } else {
                    stdhigh[stdhigh.size - 2] += 24
                }

            }

            return stdhigh

        } else {
            stdlow[stdlow.size - 1] = 24 * noteCount

            return stdlow
        }

    }

    private
            /**
             * Mostly kept as a reference of the format
             * @return
             */
    fun zeroNote(): IntArray {
        val output = IntArray(16)

        output[0] = 6 //start time in ppq
        output[1] = 0
        output[2] = 0
        output[3] = 0

        output[4] = 0 //slide note if 1. normal = 0
        output[5] = 64
        output[6] = 0
        output[7] = 0

        output[8] = 0 //length in ppq steps
        output[9] = 0
        output[10] = 0
        output[11] = 0

        output[12] = 0    // = pitch
        output[13] = 0    // = ??
        output[14] = 0    // = ??
        output[15] = 0    // = ??

        output[16] = 0 // = ??
        output[17] = 0    // = ??
        output[18] = 0    // = release
        output[19] = 0    // = first digit=portamento mode, second digit = note color

        output[20] = 0    // = panning
        output[21] = 0    // = velocity
        output[22] = 0 // = modx;
        output[23] = 0 // = mody;

        return output
    }


    private fun notedata(pitch: Int, starttime: Double, duration: Double): IntArray {
        val output = standardNote

        val start = (starttime * 96.0 * 2.0).toInt()
        val len = (duration * 96.0 * 2.0).toInt()

        //will be truncated when changing to byte later.
        output[0] = start % 256 //start time in ppq
        output[1] = start / 256 % 256
        output[2] = start / (256 * 256) % 256
        output[3] = start / (256 * 256 * 256) % 256

        //last ones


        output[8] = len % 256 //length in ppq steps
        output[9] = len / 256 % 256
        output[10] = len / (256 * 256) % 256
        output[11] = len / (256 * 256 * 256) % 256


        output[12] = pitch + 12    // = pitch
        output[13] = 0
        output[14] = 0
        output[15] = 0


        output[21] = 100    // = velocity


        return output
    }


    private fun noteData(tln : TimelineNote) : IntArray{
        val output = standardNote

        val start = (tln.location.toDouble() * 96 * 4).toInt()
        val len = (tln.duration.toDouble() * 96 * 4).toInt()

        //will be truncated when changing to byte later.
        output[0] = start % 256 //start time in ppq
        output[1] = start / 256 % 256
        output[2] = start / (256 * 256) % 256
        output[3] = start / (256 * 256 * 256) % 256

        //last ones


        output[8] = len % 256 //length in ppq steps
        output[9] = len / 256 % 256
        output[10] = len / (256 * 256) % 256
        output[11] = len / (256 * 256 * 256) % 256


        output[12] = tln.p.value    // = pitch
        output[16] = 120


        output[19] = kotlin.math.abs(tln.instrument.value % 16) //instrument

        output[21] = min(127, (127*tln.velocity.value).toInt())    // = velocity
        output[22] = (tln.paramX.value*255).toInt() // = modx;
        output[23] = (tln.paramY.value*255).toInt() // = mody;

        return output
    }


    fun int2byte(input: IntArray): List<Byte> {
        val output = ByteArray(input.size)
        for (i in input.indices) {
            output[i] = input[i].toByte()
        }
        return output.toList()
    }


    fun FLScoreExport(notes: Collection<TimelineNote>): ByteArray {

        val extraLen = if(notes.size>5) 1 else 0

        val out = mutableListOf<Byte>()
        out.addAll(int2byte(header))
        out.addAll(int2byte(fileLengthInfo(notes.size * 24 + 18 + extraLen)))
        out.addAll(int2byte(somestuff(notes.size)))

        for (n in notes) {

            out.addAll(int2byte(noteData(n)))
        }

        return out.toByteArray()


    }


}