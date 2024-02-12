package fr.unilim.codelinguo.view

import fr.unilim.codelinguo.persistence.lang.JsonLangDao
import fr.unilim.codelinguo.persistence.lang.LangDAO
import fr.unilim.codelinguo.view.style.ViewStyles
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import tornadofx.*

class CreateProjectView : View() {
    private lateinit var projectNameTextField: TextField
    private val lang: LangDAO = JsonLangDao()

    override val root = vbox {
        alignment = Pos.CENTER
        paddingAll = 10.0
        spacing = 10.0

        label(lang.getMessage("project_name_label")) {
            addClass(ViewStyles.heading)
            padding = Insets(0.0, 0.0, 20.0, 0.0)
        }

        projectNameTextField = textfield {
            promptText = lang.getMessage("project_new_name")
            addClass(ViewStyles.customTextField)
        }

        region {
            prefHeight = 20.0
            vgrow = Priority.ALWAYS
        }

        button(lang.getMessage("button_validate")) {
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
