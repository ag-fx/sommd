package org.domaintbn.sommd.gui

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.scene.input.*
import javafx.scene.layout.Region
import javafx.scene.text.Font
import javafx.stage.*
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.StyleClassedTextArea
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.fxmisc.richtext.util.UndoUtils
import org.domaintbn.sommd.core.MessageRepository
import org.domaintbn.sommd.core.musical.TimelineNote
import org.domaintbn.sommd.core.parsing.*
import org.domaintbn.sommd.core.parsing.parserimplementations.RootParser
import org.domaintbn.sommd.core.synth.AudioRender
import tornadofx.*
import java.io.File
import java.util.*

class EditorView : View("Editor") {

    private val fileExporter : FileExporter by inject()
    private val compilerStateLabelView : CompilerStateLabelView by inject()
    private val messageArea : MessageAreaView by inject()

    private val codeAreaEditor = StyleClassedTextArea()

    private val bpmValue = SimpleStringProperty("BPM\n120")


    val timerForCompiling = Timeline()

    val timerForErrorHighlight = Timeline().apply {
        this.keyFrames.add(KeyFrame(javafx.util.Duration.millis(350.0), EventHandler<ActionEvent>() {
            markError()
        }))
        this.setCycleCount(Timeline.INDEFINITE)
        this.play()
    }

    val exportingOK = SimpleBooleanProperty(false)
    val errorToShow = SimpleBooleanProperty(false)

    var errorToken: SubString? = null
    var lastErrorToken: SubString = SubString("")



    val timelineDelayedExportEnable = Timeline().apply{
        keyFrames.clear()
        keyFrames.add(KeyFrame(javafx.util.Duration.millis(200.0), EventHandler<ActionEvent>() {
            exportingOK.value = true
        }))
    }


    override val root = borderpane {

        top = hbox {
            vbox {
                menubar {
                    menu("File") {
                        item("New") {
                            action {
                                //resetFile()
                                newFile()
                            }
                        }
                        item("Open..") {
                            action {
                                loadFile(this@EditorView.currentWindow)

                            }
                        }
                        separator()
                        item("Save As..") {
                            action {
                                saveFile(this@EditorView.codeAreaEditor.text,this@EditorView.currentWindow)

                            }
                        }

                        separator()
                        item("Quit") {
                            action {
                                find(MainView::class).quitProgramDialog()
                            }
                        }
                    }
                    menu("Misc") {
                        item("Cycle fonts") {
                            var idx = 0
                            //val fontNames = listOf(Font.getDefault().name,"Liberation Mono","Noto Serif","Noto Sans","Liberation Serif","SansSerif","Monospaced")
                            val fontNames = listOf(Font.getDefault().name, "Monospaced")
                            action {
                                this@EditorView.codeAreaEditor.style {
                                    idx = (idx + 1) % fontNames.size
                                    //font = Font.font(fontNames[idx])
                                    fontFamily = Font.font(fontNames[idx]).family
                                    messageArea.setMessage("Changed to font: ${fontNames[idx]}")
                                    reloadStylesheets()
                                }
                            }
                        }
                        item("Cycle font size") {
                            var idx = 0
                            val fontSizes = listOf(10, 12, 14, 16)
                            action {
                                this@EditorView.codeAreaEditor.style {
                                    idx = (idx + 1) % fontSizes.size
                                    fontSize = (fontSizes[idx]).px
                                    reloadStylesheets()
                                }
                            }
                        }
                        item("Cycle UI"){
                            var idx = 0
                            val styleClasses = listOf(MainStyle::class,MainStyle2::class)
                            action{
                                removeStylesheet(styleClasses[idx])
                                idx = (idx+1) % styleClasses.size
                                importStylesheet(styleClasses[idx])
                            }
                        }
                        separator()

                        item("Reset window size"){
                            enableWhen(this@EditorView.primaryStage.maximizedProperty().not())
                            action {
                                this@EditorView.primaryStage.width = constants.MIN_WIDTH
                                this@EditorView.primaryStage.height = constants.MIN_HEIGHT
                            }
                        }
                        item("Fix not-resizable") {
                            action {
                                find(MainView::class).fixNotResizable()
                            }
                        }
                    }

                }
                separator(orientation = Orientation.HORIZONTAL)
            }
            hbox {
                addClass(CustomCssStrings.buttonhboxgroup)
                button {
                    val unicode_mouseicon = "\uD83D\uDDB0"
                    text = "Export\nMIDI $unicode_mouseicon"
                    action {
                        fileExporter.exportMidiFile(currentTimeline, this@EditorView.currentWindow)
                    }
                    enableWhen(exportingOK)

                    this.setOnDragDetected {
                        if (exportingOK.value && fileExporter.preExportFile(currentTimeline)) {
                            //it.consume()
                            val db = startDragAndDrop(TransferMode.COPY)
                            val cbc = ClipboardContent()
                            cbc.putFiles(listOf(File("dragDropMIDI.mid")))
                            db.setContent(cbc)
                            it.consume()
                        }
                        //it.consume()
                    }

                }
                button {
                    val unicode_mouseicon = "\uD83D\uDDB0"
                    text = "Export$unicode_mouseicon\nFL Score"
                    action {
                        fileExporter.exportFLScoreFile(currentTimeline, this@EditorView.currentWindow)
                    }
                    enableWhen(exportingOK)

                    this.setOnDragDetected {
                        if (exportingOK.value && fileExporter.preExportFile(currentTimeline)) {
                            val db = startDragAndDrop(TransferMode.COPY)
                            val cbc = ClipboardContent()
                            cbc.putFiles(listOf(File("dragDropFLScore.fsc")))
                            db.setContent(cbc)
                            it.consume()
                        }
                    }

                }
            }

            hbox {
                addClass(CustomCssStrings.buttonhboxgroup)

                button(bpmValue) {
                    action {
                        val tempoDialog = find(TempoSelectDialog::class)
                        tempoDialog.externalBpmProperty = this@EditorView.bpmValue
                        find(MainView::class).showFakeModalDialog(tempoDialog)
                    }

                }

                button("Export\nAudio") {
                    enableWhen(exportingOK)
                    action {
                        exportAudioAction()
                    }
                }

                button("Play\nMIDI") {
                    val playMode = SimpleBooleanProperty(true)
                    enableWhen(exportingOK.or(playMode.not()))
                    action {
                        if (playMode.value) {
                            val tempoBPM = find(TempoSelectDialog::class).tempoBPM
                            find(JavaSynthPlayback::class).stopSequencer()
                            find(JavaSynthPlayback::class).startSequencer(currentTimeline, tempoBPM)
                            playMode.value = !playMode.value
                            this.text = "Stop\nMIDI"
                        } else {
                            find(JavaSynthPlayback::class).stopSequencer()
                            this.text = "Play\nMIDI"
                            playMode.value = !playMode.value
                        }
                    }
                }
            }


            spacer()

            button("Goto\nError") {
                enableWhen(errorToShow)
                action {
                    val tmp = errorToken

                    if (tmp != null) {
                        codeAreaEditor.selectRange(tmp.range.start, tmp.range.start + tmp.string.length)
                        codeAreaEditor.requestFocus()
                        codeAreaEditor.requestFollowCaret()
                    } else {
                    }
                }
            }
            this += compilerStateLabelView



            this.children.filter { it is Region }.forEach {
                (it as Region).useMaxHeight = true
            }
        }

        center = borderpane {

            center = VirtualizedScrollPane<StyleClassedTextArea>(codeAreaEditor)
            bottom = borderpane {
                top = separator(Orientation.HORIZONTAL)
                center = messageArea.root
            }

        }
    }

    private fun exportAudioAction() {
        val output = File("output.wav")

        val callback : (Boolean) -> Unit = {
            if(it) {
                val timeMultiplier = find(TempoSelectDialog::class).timeMultiplier

                runAsync {
                    val afe = AudioRender(this@EditorView.currentTimeline, timeMultiplier)

                    val sampleArray = afe.render(44100.0)
                    fileExporter.audioBuffer2WAVFile(sampleArray, 44100.0, File("output.wav"))
                } ui {

                    messageArea.setMessage("Export to audio done: ${output.absolutePath}")
                }
            } else {
                messageArea.setMessage("Export canceled")
            }
        }

        find(MainView::class).showFakeModalDialog("Export a chiptune-esque audio rendering now?",callback)

    }

    init {
        setupCodeEditor()
    }

    private fun resetText(str: String) {

        val delay = when (str.length) {
            in 0..500 -> 50.0
            in 500..2000 -> 200.0
            in 2000..5500 -> 500.0
            in 5500..25500 -> 1000.0
            else -> 2500.0
        }

        messageArea.setMessage("...waiting")
        compilerStateLabelView.showCompilerStateWaiting()


        this.errorToShow.value = false
        timelineDelayedExportEnable.stop()
        this.exportingOK.value = false


        this.errorToken = null
        this.markError()


        compilerTask?.cancel()

        val highlightTask = highlightingTask
        if (highlightTask != null) {
            highlightTask.cancel()
        }


        timerForCompiling.stop()
        timerForCompiling.keyFrames.clear()
        timerForCompiling.keyFrames.add(KeyFrame(javafx.util.Duration.millis(delay), EventHandler<ActionEvent>() {
            startCompileTask(str)
            startHighlightingTask(str)
        }))
        timerForCompiling.play()

        timerForErrorHighlight.playFromStart()
    }


    private var compilerTask: FXTask<*>? = null

    private var highlightingTask: FXTask<*>? = null

    private fun startCompileTask(str: String) {

        compilerStateLabelView.showCompilerStateWorking()


        fun localCompile(callBack: () -> Unit): CompilationResult {
            val lex = Lexer(str, { callBack() })
            //val lex = Lexer(sourceText.value)
            val rp = RootParser()
            try {
                rp.start(lex)
                val mp = MasterPlayhead().process(rp.getCommandSequence())

                return CompilationResult(mp, null, false)
            } catch (pe: ParserException) {

                return CompilationResult(emptyList(), pe, false)
            } catch (ie: ParsingInterruptedException) {
                return CompilationResult(emptyList(), null, true)
            }
        }

        runAsync() {
            fun callback() {
                if (this.isCancelled) {
                    throw ParsingInterruptedException()
                }
                //this.updateProgress(progress,1.0)

            }
            this@EditorView.compilerTask = this

            localCompile({ callback() })
        } ui {
            this.currentTimeline.clear()
            this.currentTimeline.addAll(it.outputNotes)
            when {
                it.aborted -> {
                    messageArea.setOutputMessageDelayed(currentTimeline)
                    compilerStateLabelView.showCompilerStateWaiting()
                }
                it.parserException != null -> {
                    compilerStateLabelView.showCompilerStateError()
                    this.errorToShow.value = true

                    this.errorToken = it.parserException.errorSpot

                    //messageArea.setMessage((it.parserException.errorSpot.string!!))
                    messageArea.setErrorMessageDelayed(it.parserException)
                }
                else -> {
                    compilerStateLabelView.showCompilerStateOK()
                    this.exportingOK.value = currentTimeline.isNotEmpty() && false;
                    if(currentTimeline.isNotEmpty()) {
                        timelineDelayedExportEnable.playFromStart()
                    }
                    messageArea.setOutputMessageDelayed(currentTimeline)
                }
            }


        }


    }

    private fun setupCodeEditor() {

        this.codeAreaEditor.apply {
            this.stylesheets.clear()

            this.replaceText(MessageRepository.versionMessage)
            this.textProperty().onChange {
                val s = it ?: ""
                resetText(s)
            }
            this.setParagraphGraphicFactory(LineNumberFactory.get(codeAreaEditor))

            this.undoManager = UndoUtils.plainTextUndoManager(this, java.time.Duration.ofMillis(400))

            resetText(this.text)
        }
    }

    private fun newFile(){
        val msg = "Discard contents and open new file?"
        val callback : (Boolean) -> Unit = {confirmed : Boolean ->
          if(confirmed){
              codeAreaEditor.replaceText("")
              codeAreaEditor.undoManager.forgetHistory()
          }
        }
        find(MainView::class).showFakeModalDialog(msg,callback)
    }


    private fun loadFile(parentWindow : Window?) {


        val extFilt = FileChooser.ExtensionFilter("TXT file", "*.txt")

        val tmp = chooseFile("",arrayOf(extFilt),mode=FileChooserMode.Single, owner=parentWindow)
        if(tmp.isNotEmpty()) tmp.first().apply{
            codeAreaEditor.replaceText(this.readText())
            codeAreaEditor.undoManager.forgetHistory()
        }


        find(MainView::class).fixNotResizable()

        //mc.tryCompilation()
        //find(MainView::class).fakeModal(false)
    }

    private fun saveFile(textContents : String, parentWindow : Window?) {
        val fc = FileChooser()
        fc.extensionFilters.add(FileChooser.ExtensionFilter("TXT file", "*.txt"))
        val result = FileChooser().showSaveDialog(parentWindow)

        if (result != null) {
            val f = File(result.path)
            f.writeBytes(textContents.toByteArray())
            //messageText.value = "Saved file to ${f.toString()}"
        }

        find(MainView::class).fixNotResizable()
    }


    private val currentTimeline = mutableListOf<TimelineNote>()


    private fun startHighlightingTask(text: String) {

        runAsync {
            fun callBack() {
                if (this.isCancelled) {
                    throw ParsingInterruptedException()
                }
            }
            highlightingTask = this
            find(SyntaxSpanBuilder::class).computeHighlight(text, { callBack() })
        } ui {
            if(text.length != codeAreaEditor.text.length){
               // println("Changed under my nose!") // TODO bug
            }else{
            codeAreaEditor.setStyleSpans(0, it)
        }}

    }

    private fun markError() {
        val errorSpot = this.errorToken
        if (errorSpot != null) {
            this.lastErrorToken = errorSpot
            val ssb = StyleSpansBuilder<Collection<String>>()

            val len = errorSpot.string.length

            val styleClass = "syntaxerror"
            ssb.add(Collections.singleton(styleClass), len)
            this.codeAreaEditor.setStyleSpans(errorSpot.range.start, ssb.create())
        }else{
            //this.codeAreaEditor.setStyleSpans(lastErrorToken.range.start,lastErrorToken.range.endInclusive,)
        }

    }

    fun overwriteWithExample(value: String) {
        val callback: (confirmed: Boolean) -> Unit = { c ->
            when(c){
                true ->{
                    this.codeAreaEditor.replaceText(value)
                    find(MainView::class).changeToEditor()
                }
                else -> {}
            }
        }
        find(MainView::class)
                .showFakeModalDialog("Overwrite contents in editor?",callback)

    }


}