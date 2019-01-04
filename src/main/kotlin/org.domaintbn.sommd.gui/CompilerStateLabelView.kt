package org.domaintbn.sommd.gui

import tornadofx.*

class CompilerStateLabelView : View("CompilerStateLabel"){
    private val compilerStateLabel = label{

    }
    override val root = this.compilerStateLabel

    init{
        showCompilerStateWaiting()
    }


    fun showCompilerStateWaiting() {
        this.compilerStateLabel.text = "Compilation\n..waiting"
        this.compilerStateLabel.styleClass.apply {
            clear()
            add(CustomCssStrings.compLabelWait)
        }
    }

    fun showCompilerStateWorking() {
        this.compilerStateLabel.text = "Compilation\n...working"
        this.compilerStateLabel.styleClass.apply {
            clear()
            add(CustomCssStrings.compLabelWait)
        }
    }

    fun showCompilerStateOK() {
        this.compilerStateLabel.text = "Compilation\nOK!"
        this.compilerStateLabel.styleClass.apply {
            clear()
            add(CustomCssStrings.compLabelOK)
        }
    }

    fun showCompilerStateError() {
        this.compilerStateLabel.text = "Compilation\nerror!"

        this.compilerStateLabel.styleClass.apply {
            clear()
            add(CustomCssStrings.compLabelErr)
        }
    }
}