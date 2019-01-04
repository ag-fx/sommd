package org.domaintbn.sommd.gui

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import org.domaintbn.sommd.core.ExampleSongs
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.StyleClassedTextArea
import tornadofx.*


class ExamplesView : View("Examples") {



    private val highlighter : SyntaxSpanBuilder by inject()

    private val exampleTextProperty = SimpleStringProperty()

    private val exampleTextArea = StyleClassedTextArea().apply {

        stylesheets.clear()

        //addClass(MyTextAreaStyle.selection)
        exampleTextProperty.onChange {
            this.replaceText(it)
            if(it!=null) {
                startHighlightingTask(it)
            }
        }
//        style{
//            padding = box(10.px)
//        }
        this.setParagraphGraphicFactory(LineNumberFactory.get(this))

        exampleTextProperty.value = "// Select an example above.\n// The examples may be copy pasted into the editor."
        isEditable = false
    }

    override val root = borderpane {
        useMaxSize = true
        vgrow = Priority.ALWAYS

        top = hbox {


            spacer()
            label("Select example:") {
                textAlignment = TextAlignment.CENTER
                useMaxHeight = true
            }

            menubar{
                menu("Basic"){
                    for(es in ExampleSongs.values().filter{"Basic" in it.description}){
                        this += createMenuItemExampleSelect(es)
                    }
                }
                menu("Advanced"){
                    for(es in ExampleSongs.values().filter{"Advanced" in it.description}){
                        this += createMenuItemExampleSelect(es)
                    }
                }
                menu("Demo Songs"){
                    for(es in ExampleSongs.values().filter{"Demo" in it.description}){
                        this += createMenuItemExampleSelect(es)
                    }
                }
            }



//            combobox<ExampleSongs> {
//                items.addAll(ExampleSongs.values())
//                this.selectionModel.selectedItemProperty().onChange {
//                    if (it == null) {
//
//                    } else {
//                        exampleTextProperty.value = it.text
//                    }
//                }
//                cellFormat {
//                    text = it.description
//                }
//
//
//            }
            spacer()
        }
        center = VirtualizedScrollPane<StyleClassedTextArea>(exampleTextArea).apply{

        }

    }

    private fun startHighlightingTask(text: String) {


        runAsync{

            highlighter.computeHighlight(text,{})
        } ui{
            this.exampleTextArea.setStyleSpans(0, it)
        }

    }

    private fun createMenuItemExampleSelect(es : ExampleSongs): MenuItem {
        val out = MenuItem().apply{
            this.text = es.description
            action{
                exampleTextProperty.value = es.text
            }
        }
        return out

    }
}