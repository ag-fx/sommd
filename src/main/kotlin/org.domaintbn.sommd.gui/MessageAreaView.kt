package org.domaintbn.sommd.gui

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.layout.Priority
import javafx.util.Duration
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.CommandStrings
import org.domaintbn.sommd.core.parsing.ErrorMessage
import org.domaintbn.sommd.core.parsing.ParserException
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.StyleClassedTextArea
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import tornadofx.*
import java.util.*

class MessageAreaView : View("MessageAreaView"){
    private val messageArea = StyleClassedTextArea().apply{
        isEditable = false
        isWrapText = true


        style {

        }
//        this.textProperty().onChange {
//            if(it!=null){
//                //this.setStyleClass(0,it.length,"comment")
//            }
//        }
    }

    private var lastErrorEvent : ParserException? = null
    private var lastNoteList : List<TimelineNote> = emptyList()


    override val root  = borderpane{
        center = VirtualizedScrollPane<StyleClassedTextArea>(messageArea).apply{
    }
        style{
            padding = box(5.px)
        }
        minHeight = 120.0
    }


    fun setMessage(message : String){
        messageArea.replaceText(message)
        messageArea.setStyleClass(0,message.length,"comment")
    }

    private fun setErrorMessage(pe : ParserException){
        val message = pe.errorMessage


        val parserID = pe.errorSource.parserSignature+" command"
        val problemDescr = ", ${message.description}: "
        val errorArea = pe.errorSpot.toStringNoNewlines()+"\n"

        val spansBuilder = StyleSpansBuilder<Collection<String>>()
        spansBuilder.add(Collections.singleton("command"), parserID.length)
        spansBuilder.add(Collections.singleton("none"), problemDescr.length)
        spansBuilder.add(Collections.singleton("syntaxerror"), errorArea.length)


        val newText = StringBuilder()
        newText.append(parserID+ problemDescr+errorArea)
        val idxRegularFormatting = newText.length

        newText.append("-- " + message.cause+"\n")
        newText.append("-- " + message.suggestion+"\n")

        spansBuilder.add(Collections.singleton("comment"),newText.length-idxRegularFormatting)

        messageArea.replaceText(newText.toString())
        messageArea.setStyleSpans(0,spansBuilder.create())
        messageArea.selectRange(0,0)
    }

    private fun setOutputMessage(notes : List<TimelineNote>){
        val newText = StringBuilder()
        newText.append("Available commands:\n\t${availableCommandsString}\n")
        if(notes.isEmpty()){
            newText.append("Empty output. Need a NOTE (nt) command")
        } else if(notes.any{!it.p.isExportable}){
            newText.append("Warning. Some notes were outside of range for exporting.\n")
        }else{
            newText.append("Success. Note output below:\n")
        }
        notes.forEach { newText.appendln("\t"+it.printMe()) }
        messageArea.replaceText("")
        messageArea.setStyleClass(0,0,"comment")
        messageArea.replaceText(newText.toString())
        messageArea.selectRange(0,0)

    }

    private val availableCommandsString : String get(){
        val out = StringBuilder()
        val commandStrList = CommandStrings.values()
        for(k in commandStrList.indices){
            val separator = if(k== commandStrList.size-1) "" else " "
            out.append("{"+commandStrList[k].txt+"}"+separator)
        }

        return out.toString()
    }



    private val timelineError = Timeline().apply{
        keyFrames.add(KeyFrame(Duration.millis(800.0),EventHandler<ActionEvent>(){
            val pe = lastErrorEvent
            if(pe!=null) setErrorMessage(pe)
        }))
    }

    private val timelineOutput = Timeline().apply{
        keyFrames.add(KeyFrame(Duration.millis(800.0),EventHandler<ActionEvent>(){
            setOutputMessage(lastNoteList)
        }))
    }



    fun setErrorMessageDelayed(pe : ParserException){
        this.lastErrorEvent = pe

        timelineError.stop()
        timelineOutput.stop()

        timelineError.playFromStart()
    }

    fun setOutputMessageDelayed(noteList : List<TimelineNote>){
        this.lastNoteList = noteList

        timelineError.stop()
        timelineOutput.stop()

        timelineOutput.playFromStart()
    }


}