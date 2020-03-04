package org.domaintbn.sommd.gui

import javafx.scene.control.Alert
import javafx.stage.FileChooser
import javafx.stage.Window
import org.domaintbn.sommd.core.musical.FLScoreWriter
import org.domaintbn.sommd.core.musical.MIDIExport
import org.domaintbn.sommd.core.musical.TimelineNote
import tornadofx.Controller
import tornadofx.alert
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class FileExporter : Controller(){
    fun audioBuffer2WAVFile(audiobuffer: DoubleArray, samplingrate: Double, filedest: File) {
        val byteBuffer = ByteArray(audiobuffer.size * 2)
        var bufferIndex = 0
        var i = 0
        while (i < byteBuffer.size) {
            val x = (audiobuffer[bufferIndex++] * 32767.0).toInt()
            byteBuffer[i] = x.toByte()
            i++
            byteBuffer[i] = x.ushr(8).toByte()
            i++
        }

        val out = filedest
        val bigEndian = false
        val signed = true
        val bits = 16
        val channels = 1
        val format: AudioFormat
        format = AudioFormat(samplingrate.toFloat(), bits, channels, signed, bigEndian)


        val bais = ByteArrayInputStream(byteBuffer)

        val audioInputStream: AudioInputStream
        audioInputStream = AudioInputStream(bais, format, audiobuffer.size.toLong())
        try {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out)
            audioInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            throw Exception(e.message)
        }

    }


    fun exportMidiFile(currentTimeline : List<TimelineNote>, parentWindow: Window?) {
        if (currentTimeline.isEmpty()) {

            alert(
                Alert.AlertType.INFORMATION,
                "warn",
                "Nothing to export. Canceling."
            )

        } else {
            val fc = FileChooser()
            fc.extensionFilters.add(FileChooser.ExtensionFilter("MIDI file (*.mid)", "*.mid"))
            val result = FileChooser().showSaveDialog(parentWindow)


            if (result != null) {
                val validFileEnding = result.name.endsWith(".midi")
                        || result.name.endsWith(".mid")
                if (!validFileEnding) {
                    alert(Alert.AlertType.ERROR, "Error", "Wrong file ending.")
                    return
                }

                val toWrite = MIDIExport.Export(currentTimeline)
                result.writeBytes(toWrite)
                find(MessageAreaView::class).setMessage("Saved file to ${result.path}")
            }

        }
        find(MainView::class).fixNotResizable()

    }


    fun exportFLScoreFile(currentTimeline : List<TimelineNote>, parentWindow: Window?) {
        if (currentTimeline.isEmpty()) {

            alert(
                Alert.AlertType.INFORMATION,
                "warn",
                "Nothing to export. Canceling."
            )
        } else {
            val fc = FileChooser()
            fc.extensionFilters.add(FileChooser.ExtensionFilter("FL Score file (*.fsc)", "*.fsc"))
            val result = FileChooser().showSaveDialog(parentWindow)


            if (result != null) {
                val validFileEnding = result.name.endsWith(".fsc")
                if (!validFileEnding) {
                    alert(Alert.AlertType.ERROR, "Error", "Wrong file ending.")
                }else{
                    val toWrite = FLScoreWriter.FLScoreExport(currentTimeline)
                    result.writeBytes(toWrite)
                    find(MessageAreaView::class).setMessage("Saved file to ${result.path}")
                }
            }
        }
        find(MainView::class).fixNotResizable()
    }

    fun preExportFile(currentTimeline : List<TimelineNote>): Boolean {
        if (currentTimeline.isEmpty()) {
            alert(
                Alert.AlertType.INFORMATION,
                "warn",
                "Nothing to export. Canceling."
            )
            return false
        } else {
            val midiFile = File("dragDropMIDI.mid")
            val flscoreFile = File("dragDropFLScore.fsc")

            val midiToWrite = MIDIExport.Export(currentTimeline)
            val flscoreToWrite = FLScoreWriter.FLScoreExport(currentTimeline)

            midiFile.writeBytes(midiToWrite)
            flscoreFile.writeBytes(flscoreToWrite)

            return true
        }
    }
}
