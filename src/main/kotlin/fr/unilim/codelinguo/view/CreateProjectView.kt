package fr.unilim.codelinguo.view

import fr.unilim.codelinguo.persistence.lang.JsonLangDao
import fr.unilim.codelinguo.persistence.lang.LangDAO
import fr.unilim.codelinguo.view.style.ViewStyles
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
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
                if (projectNameTextField.text.trim().isBlank()) {
                    alert(
                        Alert.AlertType.WARNING,
                        lang.getMessage("project_name_error_title"),
                        lang.getMessage("project_name_blank_error_message")
                    )
                } else {
                    primaryStage.close()
                    find(MainView::class, mapOf(MainView::projectName to projectNameTextField.text.trim())).openWindow()
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        projectNameTextField.clear()
    }

}
