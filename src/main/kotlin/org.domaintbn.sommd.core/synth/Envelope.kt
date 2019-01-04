package org.domaintbn.sommd.core.synth

class Envelope(startval: Double, midval: Double, endval: Double, midpos: Double) : EnvelopeInterface {


    val startval: Double
    val midval: Double
    val endval: Double
    val midpos: Double

    init {
        this.startval = restrict(startval)
        this.midval = restrict(midval)
        this.endval = restrict(endval)
        this.midpos = restrict(midpos) //make sure
    }


    /* (non-Javadoc)
	 * @see com.abc.musicml1.SynthEnvelopeInterface#getEnvValue(double)
	 */
    override fun getEnvValue(position: Double): Double {
        val restrictedPosition = restrict(position)
        val relativePos: Double
        if (restrictedPosition <= midpos) {
            relativePos = restrictedPosition / (midpos - 0)
            return restrict(startval * (1 - relativePos) + midval * relativePos)

        } else if (restrictedPosition > midpos) {
            relativePos = (restrictedPosition - midpos) / (1 - midpos)
            return restrict(midval * (1 - relativePos) + endval * relativePos)
        }

        return startval * (1 - restrictedPosition) + endval * restrictedPosition


    }


    // TODO replace with new class "Percentage", doubles from 0 to 1
    private fun restrict(input: Double): Double {
        var output = input
        if (input > 1) {
            output = 1.0
        }
        if (input < 0) {
            output = 0.0
        }


        return output
    }


    //	public static void main(String args[]){
    //		SynthEnvelope se = new SynthEnvelope(1,0.3,0,0.1);
    //		for(int i= 0;i<100;i++){
    //			System.out.print(se.getEnvValue((i/100.0)) + ", ");
    //			if(i % 10 == 0){
    //				System.out.println("..");
    //			}
    //		}
    //	}
}
