package fr.unilim.saes5

import fr.unilim.saes5.view.MainView
import fr.unilim.saes5.view.ProjectView
import fr.unilim.saes5.view.ViewStyles
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class MainApp : App(ProjectView::class, ViewStyles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            isResizable = false
            isMaximized = false
        }
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<MainApp>(args)
}
