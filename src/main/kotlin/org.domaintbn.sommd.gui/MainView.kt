package org.domaintbn.sommd.gui

import javafx.application.Platform
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import tornadofx.View
import tornadofx.*

object constants {
    const val MIN_WIDTH = 650.0
    const val MIN_HEIGHT = 600.0
}
class MainView : View("SoMMD"){
    init {
        this.primaryStage.hide()
    }

    val tabPane = tabpane {

        this.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        this.side = Side.RIGHT
        tab("Editor") {
            this.content = find(EditorView::class).root
            //this.content = label("hei")
        }

        tab("Examples") {

            this.content = find(ExamplesView::class).root
        }
    }

    val coveringPane = borderpane{

        style{
            backgroundColor += Color(0.2,0.2,0.2,0.8)
        }
    }


    override val root = stackpane {
        this.add(tabPane)
        this.add(coveringPane.apply{
            isVisible = false
        })

    }

    fun quitProgram() {
        val result = alert(Alert.AlertType.CONFIRMATION, "Quit", "Quit now?", owner = this.currentWindow)
        val doQuit = (result.result == ButtonType.OK)

        if (doQuit) {
            find(JavaSynthPlayback::class).stopSequencer()
            Platform.exit()
        }
    }

    fun quitProgramDialog(){
        val msg = "Quit now?"
        val callback = {confirmed : Boolean ->
            if(confirmed){
                find(JavaSynthPlayback::class).stopSequencer()
                Platform.exit()
            }
        }
        showFakeModalDialog(msg,callback)
    }


    fun changeToEditor(){
        this.tabPane.selectionModel.select(0)
    }


    fun fixNotResizable(){
        val ps = this.primaryStage
        val oldX = ps.x
        val oldY = ps.y
        val oldWidth = ps.width
        val oldHeight = ps.height

        ps.hide()
        ps.isResizable = true;
        ps.x = oldX
        ps.y = oldY
        ps.width = oldWidth
        ps.height = oldHeight
        ps.show()
    }


    /**
     * dialog must contain a control that can call
     * fakeModalOff()
     */
    private fun fakeModalOn(dialog : Parent){
        coveringPane.isVisible = true
        coveringPane.center = dialog
        dialog.requestFocus()

    }

    private fun fakeModalOff(){
        coveringPane.isVisible = false
        coveringPane.center = null
    }


    fun showFakeModalDialog(msg: String, actionCallBack: (confirmed: Boolean) -> Unit) {
        val dialog = group {
            addEventFilter(KeyEvent.KEY_PRESSED
            ) { event ->
                println(event.code)
                if (event.code == KeyCode.ESCAPE) {
                    event.consume()
                    actionCallBack(false)
                    fakeModalOff()
                }
            }
            vbox {
                alignment = Pos.CENTER
                addClass(CustomCssStrings.fakemodaldialog)
                label(msg)
                hbox {
                    //                    addClass(CustomCssStrings.showBorder)
                    alignment = Pos.CENTER
                    button("OK"  ) {
                        minWidth = 100.0
                        useMaxWidth = true
                        hgrow = Priority.ALWAYS
                        action {
                            actionCallBack(true)
                            fakeModalOff()
                        }
                    }

                    button("Cancel") {
                        minWidth = 100.0
                        useMaxWidth = true
                        hgrow = Priority.ALWAYS
                        action {
                            actionCallBack(false)
                            fakeModalOff()
                        }
                    }

                }

            }
        }
        fakeModalOn(dialog)
    }

    fun showFakeModalDialog(dialog : IFakeModalDialog){
        dialog.setCallback { fakeModalOff() }

        val grp = Group()
        grp.children.add(dialog.getView().root)
        grp.addClass(CustomCssStrings.fakemodaldialog)


        fakeModalOn(grp)
    }




    init{
        this.primaryStage.initStyle(StageStyle.UNIFIED);
        //this.primaryStage.hide()
        //setWindowMinSize(constants.MIN_WIDTH,constants.MIN_HEIGHT)
        this.primaryStage.minWidth = constants.MIN_WIDTH
        this.primaryStage.minHeight = constants.MIN_HEIGHT


        this.primaryStage.setOnCloseRequest {
            it.consume()
            quitProgramDialog()
            //quitProgram()
        }

        this.primaryStage.show()
//        this.root.addClass(StyleFactory.getStyle1().simpleName ?: "")
    }


}
