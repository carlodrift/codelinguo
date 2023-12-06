package fr.unilim.saes5

import fr.unilim.saes5.view.MainView
import fr.unilim.saes5.view.Styles
import tornadofx.App
import tornadofx.launch

class MainApp : App(MainView::class, Styles::class)

fun main(args: Array<String>) {
    launch<MainApp>(args)
}
