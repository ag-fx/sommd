package org.domaintbn.sommd.gui

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*


fun main(){

    try {
        //start TornadoFX application
        javafx.application.Application.launch(App::class.java)
    }
    catch(exp : ClassNotFoundException){
        showWrongJREMessage()
        throw exp
    }
    catch(exp : NoClassDefFoundError){
        showWrongJREMessage()
        throw exp
    }
}


private fun showWrongJREMessage(){
    SwingUtilities.invokeLater {
        val frame = JFrame()
        val message = "Because of a dependency on the TornadoFX framework,\n" +
                "this program will only run on Java version 8.\nCheck your JRE version\n" +
                "or wait for a later version to support JRE 10+"
        frame.contentPane.add(JTextArea(message).apply{
            lineWrap = true
        }, BorderLayout.CENTER)
        frame.contentPane.add(JButton("Close").apply{
            this.addActionListener(object : ActionListener{
                override fun actionPerformed(p0: ActionEvent?) {
                    frame.dispose()
                }

            })
        },BorderLayout.SOUTH)
        frame.size = Dimension(400,400)
        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }

}