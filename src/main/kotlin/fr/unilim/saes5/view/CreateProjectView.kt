package fr.unilim.saes5.view

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.persistence.glossary.JsonGlossaryDao
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import tornadofx.*
import javax.swing.GroupLayout.Alignment

class CreateProjectView : View() {
    private lateinit var projectNameTextField: TextField

    init {
    }

    override val root = vbox {
        alignment = Pos.CENTER
        paddingAll = 10.0
        spacing = 10.0

        label("Nom du projet") {
            addClass(ViewStyles.heading)
            padding = Insets(0.0, 0.0, 20.0, 0.0)
        }

        projectNameTextField = textfield {
            promptText = "Entrez le nom du projet"
            addClass(ViewStyles.customTextField)
        }

        region {
            prefHeight = 20.0
            vgrow = Priority.ALWAYS
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
