package org.domaintbn.sommd.gui

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import javafx.util.StringConverter
import tornadofx.*

class TempoSelectDialog() : View("Audio Export"){
    var wasCanceled = false

    private var timeMultiplierLocal : Double = 120.0/(240-60)


    val timeMultiplier : Double get(){
        return 120/tempoBPM.toDouble()
    }

    val tempoBPM : Int get(){
        return multiplierToBPM(timeMultiplierLocal)
    }

    private fun multiplierToBPM(d : Double) : Int{
        return (60+(240-60)*d/2).toInt()
    }

    val tempoShowLabel = label(tempoBPM.toString()){
        hgrow = Priority.ALWAYS
        useMaxSize = true
        textAlignment = TextAlignment.CENTER
        alignment = Pos.CENTER
        style{
            text{
                textAlignment = TextAlignment.CENTER
            }
            padding = box(10.px)
        }
    }

    private var changedTimeMultiplier : Double = 1.0

    override val root = vbox{
//        style{
//            padding = box(20.px)
//        }
        this.setMinSize(300.0,150.0)
        label("Set tempo for exported audio and MIDI playback"){
           style{
                padding = box(10.px)
               text {
                   textAlignment = TextAlignment.CENTER
               }
            }
            hgrow = Priority.ALWAYS
            useMaxSize = true
        }
        this.add(tempoShowLabel)

        slider(0.0..2.0){
            this.value = timeMultiplierLocal
            this.valueProperty().onChange {
                changedTimeMultiplier = it
                tempoShowLabel.text = multiplierToBPM(changedTimeMultiplier).toString()

            }
            this.labelFormatter = object : StringConverter<Double>(){
                override fun toString(p0: Double?): String {
                    if(p0==null) return "null"
                    return when(p0){
                        in 0.0..0.5 -> "60"
                        else -> "240"
                    }
                }

                override fun fromString(p0: String?): Double {
                    error("do not use")
                    return 0.0
                }

            }
            this.isShowTickMarks = true
            this.isShowTickLabels = true
            style{
                padding = box(10.px)
            }
        }
        hbox{
            useMaxWidth = true
            style {
                padding = box(10.px)
            }

            button("Change"){
                action{
                    timeMultiplierLocal = changedTimeMultiplier
                    this@TempoSelectDialog.close()
                }
                useMaxWidth = true
                hgrow = Priority.ALWAYS
            }
            button("Cancel"){
                action{
                    this@TempoSelectDialog.close()
                    changedTimeMultiplier = timeMultiplierLocal
                    wasCanceled = true
                }
                useMaxWidth = true
                hgrow = Priority.ALWAYS
            }
        }
    }

    init{
        this.setWindowMinSize(300,150)
        this.primaryStage.isResizable = false

    }
}