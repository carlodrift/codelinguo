package fr.unilim.saes5.view

import javafx.geometry.Pos
import javafx.scene.image.Image
import tornadofx.*
import javax.swing.GroupLayout.Alignment

class CreateProjectView : View() {
    init {

    }

    override val root = vbox(5.0) {

        paddingAll = 10.0
        label("Nom du projet") {
            addClass(ViewStyles.heading)
        }

        textfield {
            promptText = "Entrez le nom du projet"
            addClass(ViewStyles.customTextField)
        }

        button("Valider") {
            addClass(ViewStyles.projectButton)
        }


        }

    }
