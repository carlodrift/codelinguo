package fr.unilim.saes5.view

import javafx.geometry.Pos
import javafx.scene.image.Image
import tornadofx.*

class ProjectView : View() {
    init {

    }

    override val root = vbox(5.0) {
        primaryStage.width = 800.0
        primaryStage.height = 600.0
        hbox (5.0){
            vbox {
                label("Projets récents") {
                    addClass(ViewStyles.heading)
                }


            }

            vbox {
                button("Nouveau projet").apply {
                    addClass(ViewStyles.projectButton)
                    graphic = javafx.scene.image.ImageView(Image("/plus.png"))
                    action { }
                }

                button("Ouvrir un projet").apply {
                    addClass(ViewStyles.projectButton)
                    graphic = javafx.scene.image.ImageView(Image("/downloads.png"))
                    action { }
                }
            }.alignment = Pos.TOP_RIGHT


        }


    }
}