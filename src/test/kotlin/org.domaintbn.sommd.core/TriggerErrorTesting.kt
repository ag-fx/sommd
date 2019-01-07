package org.domaintbn.sommd.core

import org.domaintbn.sommd.core.parsing.Lexer
import org.domaintbn.sommd.core.parsing.MasterPlayhead
import org.domaintbn.sommd.core.parsing.ParserException
import org.domaintbn.sommd.core.parsing.parserimplementations.RootParser
import org.junit.Test
import kotlin.test.fail

class TriggerErrorTesting{

    private fun failOnNoError(song: String) {
        val rp = RootParser()
        try {
            rp.start(Lexer(song))
            //println("reached here")
            fail("Didn't trigger error as it should have.")
        } catch (pe: ParserException) {
            //successfully caught error
            //println(pe.errorMessage.cause)
        }
    }

    private fun shouldNotCauseException(song : String){
        val rp = RootParser()
        rp.start(Lexer(song))
        MasterPlayhead().process(rp.rootCommSeq)
    }


    @Test
    fun error1(){
        val song = "brs nt [50 58 57 51 57 58 bre"
        failOnNoError(song)
    }

    @Test
    fun error2(){
        val song = "bre"
        failOnNoError(song)
    }

	@Test
	fun error3(){
	val song = "dr  1/15 ve -[60 80] dr 32/1 nt 0 dr 8/12 nt 0 nt 50"
        shouldNotCauseException(song)
    }

    @Test
    fun error4(){
        val song = "dr  0 ve 60 dr 1/4 nt 50"
        shouldNotCauseException(song)
    }

    @Test
    fun allExamples(){
        ExampleSongs.values()
                .filter{"Comments" !in it.description}
                .forEach { shouldNotCauseException(it.text) }
    }


}