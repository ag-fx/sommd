package org.domaintbn.sommd.gui

import javafx.scene.layout.Priority
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*


class ConfirmDialog(question : String) : View("Confirm"){

    var okPressed = false

    override val root = vbox{
        label(question){
            style{
                padding = box(15.px)
            }
            useMaxSize = true
            hgrow = Priority.ALWAYS
        }
        hbox{
            useMaxWidth = true
            hgrow = Priority.ALWAYS
            button("OK"){
                action{
                    okPressed = true
                    close()
                }
                useMaxWidth = true
                hgrow = Priority.ALWAYS
            }
            button("Cancel"){
                action{
                    close()
                }
                useMaxWidth = true
                hgrow = Priority.ALWAYS
            }
        }

    }

    companion object {
        fun getConfirmation(prompt : String): Boolean {
            val cd = ConfirmDialog(prompt)
            cd.openWindow(StageStyle.UNIFIED,block=true)
            return cd.okPressed
        }
    }
}