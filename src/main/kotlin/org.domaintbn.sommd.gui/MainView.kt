package org.domaintbn.sommd.gui

import javafx.application.Platform
import javafx.geometry.Side
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TabPane
import tornadofx.View
import tornadofx.*

class MainView : View("SoMMD"){


    override val root = tabpane{

        this.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        this.side = Side.RIGHT
        tab("Editor"){
            this.content = EditorView().root
            //this.content = label("hei")
        }

        tab("Examples"){

            this.content = ExamplesView().root
        }
    }

    fun quitProgram() {
        val result = alert(Alert.AlertType.CONFIRMATION, "Quit", "Quit now?", owner = this.currentWindow)
        val doQuit = (result.result == ButtonType.OK)

        if (doQuit) {
            find(JavaSynthPlayback::class).stopSequencer()
            Platform.exit()
        }
    }



    init{
        this.primaryStage.hide()
        setWindowMinSize(650,600)
        this.primaryStage.setOnCloseRequest {
                quitProgram()
        }
        this.primaryStage.show()
//        this.root.addClass(StyleFactory.getStyle1().simpleName ?: "")
    }


}
