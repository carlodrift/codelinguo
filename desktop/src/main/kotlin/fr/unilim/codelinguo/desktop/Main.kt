package fr.unilim.codelinguo.desktop

import fr.unilim.codelinguo.desktop.style.ViewStyles
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class MainApp : App(ProjectView::class, ViewStyles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            try {
                val imagePath = File.separator + "logo" + File.separator + "logot.png"
                val image = Image(FileInputStream(imagePath))
                icons += image
            } catch (ignored: FileNotFoundException) {
            } catch (ignored: IllegalArgumentException) {
            }

            isResizable = false
            isMaximized = false
        }
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    System.setProperty("sun.java2d.uiScale", "1.0")
    launch<MainApp>(args)
}
