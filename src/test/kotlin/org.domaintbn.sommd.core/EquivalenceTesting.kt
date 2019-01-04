package org.domaintbn.sommd.core

import org.domaintbn.sommd.core.parsing.Lexer
import org.domaintbn.sommd.core.parsing.MasterPlayhead
import org.domaintbn.sommd.core.parsing.parserimplementations.RootParser
import kotlin.test.assertEquals
import kotlin.test.Test

class EquivalenceTesting{



    fun getSongOutput(input : String) : String{
        val rp = RootParser().apply{start(Lexer(input))}
        val commseq1 = rp.rootCommSeq
        val song1 = MasterPlayhead().process(commseq1)
        val output = StringBuilder()
        song1.forEach{output.append(it.printMe()+"\n")}
        return output.toString()
    }



    @Test
    fun testBasic(){
        assertEquals(
            getSongOutput("nt 40 50 "),
            getSongOutput("dr 1/4 nt 40 50 ")
        )
    }


    @Test
    fun testBrackets(){
        assertEquals(
            getSongOutput("nt 40 40"),
            getSongOutput("nt [40 40] ")
        )
    }





    @Test
    fun testBrackets3(){

        assertEquals(
            getSongOutput("nt 40-47 50 45 42 40"),
            getSongOutput("nt -[40 47] [50 45 42] 40")
        )
    }

//    @Test
//    fun testBrackets4(){
//        val commseq1 = RootParser().start(Lexer("nt [40 50[30 35]]")).commandSeq
//    }


    @Test
    fun testRepeat2(){
        assertEquals(
            getSongOutput("nt 40 40"),
            //getSongOutput("nt [40^2]"),
            getSongOutput("nt 40^2")
        )
    }



    @Test
    fun testExtend(){
        assertEquals(
            getSongOutput("dr 1/2 nt 40"),
            //getSongOutput("nt [40^2]"),
            getSongOutput("dr 1/4 nt 40*2")
        )
    }


    @Test
    fun testExtend2(){
        assertEquals(
            getSongOutput("brs dr 1/4 nt 40 50 brr dr 1/2 nt 30 bre"),
            //getSongOutput("nt [40^2]"),
            getSongOutput("dr 1/4 brs nt 40 50 brr nt 30*2 bre")
        )

    }

    @Test
    fun testExtend3(){
        assertEquals(
            getSongOutput("dr 1/1 nt 40 dr 1/3 nt 50"),
            //getSongOutput("nt [40^2]"),
            getSongOutput("dr 1/3 2/3 nt 40*2 50")
        )

    }


    @Test
    fun testBracketRepeat(){
        assertEquals(
            getSongOutput("nt 40 50 40 50"),
            //getSongOutput("nt [40^2]"),
            getSongOutput("nt [40 50]^2")
        )
    }


    @Test
    fun durationGroups(){
        assertEquals(
            getSongOutput("dr [1/2 1/2] nt 50"),
            getSongOutput("dr 1/2 1/2 nt 50 ")
        )
    }

    @Test
    fun durationGroups2(){
        assertEquals(
            getSongOutput("dr [1/8 1/8 3/4]^2 1/1 nt 50^7"),
            getSongOutput("dr 1/8 1/8 3/4 1/8 1/8 3/4 1/1 nt 50^7 ")
        )
    }

    @Test
    fun durationGroups3(){
        assertEquals(
            getSongOutput("dr -[1/8^16] nt 50"),
            getSongOutput("dr 1/8 nt 50^16")
        )
    }

    @Test
    fun weirdScaleThing(){
        val mel = "nt 50 	52 50	!52   50 !52  !54	!52 !54 !56"
        assertEquals(
            getSongOutput(" dr 1/16 sc [50 52 53 55 57 58 5a] $mel"),
            getSongOutput("dr 1/16 sc 50-52-53-55-57-58-5a $mel")
        )
    }

    @Test
    fun groups(){
        assertEquals(
            getSongOutput("in [0^2 1 2]^3 2 1 0 3 nt 50^16"),
            getSongOutput("in 0 0 1 2 0 0 1 2 0 0 1 2 2 1 0 3 nt 50^16")
        )
    }

    @Test
    fun groups1(){
        assertEquals(
            getSongOutput("dr 1/4 nt [40*2 , 47 45]"),
            getSongOutput("brs dr 1/2 nt 40 brr dr 1/4 nt 47 45 bre")
        )
    }

}