package fr.unilim.saes5.view

import javafx.geometry.Pos
import javafx.scene.image.Image
import tornadofx.*
import javax.swing.GroupLayout.Alignment

class ProjectView : View() {
    init {

    }

    override val root = vbox(5.0) {
        primaryStage.width = 850.0
        primaryStage.height = 610.0

        vbox {
            paddingAll = 10.0
            button("Fermer X").apply {
                addClass(ViewStyles.downloadButtonHover)
                action { primaryStage.close() }
            }
        }.alignment = Pos.TOP_RIGHT

        separator { addClass(ViewStyles.separator) }

        hbox (5.0){
            vbox {
                alignment = Pos.CENTER_LEFT
                label("Projets récents") {
                    paddingLeft = 5.0
                    addClass(ViewStyles.heading)
                }
                separator { addClass(ViewStyles.separator) }

            }

            vbox(10.0) {
                alignment = Pos.CENTER_RIGHT

                button("Nouveau projet").apply {
                    addClass(ViewStyles.projectButton)
                    graphic = javafx.scene.image.ImageView(Image("/plus.png"))
                    action {
                        find(CreateProjectView::class).openWindow()
                    }
                }

                button("Ouvrir un projet").apply {
                    addClass(ViewStyles.projectButton)
                    graphic = javafx.scene.image.ImageView(Image("/downloads.png"))
                    action {
                    }
                }
            }


        }.alignment = Pos.CENTER


    }
}
