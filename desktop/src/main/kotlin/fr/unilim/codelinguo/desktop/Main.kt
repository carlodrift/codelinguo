package fr.unilim.codelinguo.desktop

import fr.unilim.codelinguo.desktop.style.ViewStyles
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import java.io.File

class MainApp : App(ProjectView::class, ViewStyles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            icons += Image(File.separator + "logo" + File.separator + "logo.png")
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
