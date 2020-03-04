package org.domaintbn.sommd.gui

import javafx.scene.control.ButtonType
import tornadofx.*

interface IFakeModalDialog {
    fun setCallback(callback: (ButtonType) -> Unit)
    fun getView() : View
}