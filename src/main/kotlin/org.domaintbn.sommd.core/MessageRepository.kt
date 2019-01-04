package org.domaintbn.sommd.core

import org.domaintbn.sommd.core.parsing.CommandStrings
import org.domaintbn.sommd.core.parsing.ParserException

class MessageRepository{

    companion object {

        val versionMessage = if (false) "nt 40x" else """
            /*Version 0.1
            Beware of some bugs and errors, but most important things should work.

            - The mouse icon on the MIDI export button means you can
                    drag and drop midi files from there!*/


            //this plays two notes. Many more examples can be found (look to the right!)
            nt 40 50
        """.trimIndent()


    }
}