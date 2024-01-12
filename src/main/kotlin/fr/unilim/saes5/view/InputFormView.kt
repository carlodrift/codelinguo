package fr.unilim.saes5.view

import  fr.unilim.saes5.service.CompletionService
import javafx.scene.control.ContextMenu
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import tornadofx.*
import java.util.*

class InputFormView(
    private val completionService: CompletionService,
    private val myBundle: ResourceBundle,
    private val activeContextMenus: MutableMap<TextField, ContextMenu> = mutableMapOf()
) : View() {

    val tokenInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_token")
    }
    val synonymInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_synonym")
    }
    val definitionInput: TextArea = textarea {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_definition")
        prefHeight = 145.0
    }
    val primaryContextInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_primary_context")
    }
    val antonymInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_antonym")
    }
    val secondaryContextInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = myBundle.getString("prompt_secondary_context")
    }

    init {
        primaryContextInput.textProperty().addListener { _, _, _ ->
            ViewUtilities.updateAutoCompletion(primaryContextInput, completionService, activeContextMenus)
        }
        secondaryContextInput.textProperty().addListener { _, _, _ ->
            ViewUtilities.updateAutoCompletion(secondaryContextInput, completionService, activeContextMenus)
        }
    }

    override val root = hbox(10.0) {
        form {
            fieldset {
                field {
                    vbox {
                        label(myBundle.getString("token_label") + myBundle.getString("required_field"))
                        this += tokenInput
                    }
                }
                field {
                    vbox {
                        label(myBundle.getString("primary_context_label") + myBundle.getString("required_field"))
                        this += primaryContextInput
                    }
                }
                field {
                    vbox {
                        label(myBundle.getString("secondary_context_label"))
                        this += secondaryContextInput
                    }
                }
            }
        }
        form {
            fieldset {
                field {
                    vbox {
                        label(myBundle.getString("synonym_label"))
                        this += synonymInput
                    }
                }
                field {
                    vbox {
                        label(myBundle.getString("antonym_label"))
                        this += antonymInput
                    }
                }
            }
        }
        form {
            fieldset {
                field {
                    vbox {
                        label(myBundle.getString("definition_label"))
                        this += definitionInput
                    }
                }
            }
        }
    }
}
