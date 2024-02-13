package fr.unilim.codelinguo

import fr.unilim.codelinguo.view.ProjectView
import fr.unilim.codelinguo.view.style.ViewStyles
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class MainApp : App(ProjectView::class, ViewStyles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            icons += Image("/logo/logo.png")
            isResizable = false
            isMaximized = false
        }
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<MainApp>(args)
}