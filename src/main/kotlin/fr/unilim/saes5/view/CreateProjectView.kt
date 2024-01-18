package fr.unilim.saes5.view

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.persistence.glossary.JsonGlossaryDao
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.image.Image
import tornadofx.*
import javax.swing.GroupLayout.Alignment

class CreateProjectView : View() {
    private lateinit var projectNameTextField: TextField

    init {
    }

    override val root = vbox(5.0) {
        paddingAll = 10.0
        label("Nom du projet") {
            addClass(ViewStyles.heading)
        }

        projectNameTextField = textfield {
            promptText = "Entrez le nom du projet"
            addClass(ViewStyles.customTextField)
        }

        button("Valider") {
            addClass(ViewStyles.projectButton)

            action {
                primaryStage.close()
                find(MainView::class, mapOf(MainView::projectName to projectNameTextField.text)).openWindow()
            }
        }
    }

    override fun onDock() {
        super.onDock()
        projectNameTextField.clear()
    }
}
