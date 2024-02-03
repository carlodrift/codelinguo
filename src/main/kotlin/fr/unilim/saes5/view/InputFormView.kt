package fr.unilim.saes5.view

import fr.unilim.saes5.persistence.lang.LangDAO
import fr.unilim.saes5.service.CompletionService
import fr.unilim.saes5.view.style.ViewStyles
import fr.unilim.saes5.view.utilities.ViewUtilities
import javafx.scene.control.ContextMenu
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import tornadofx.*

class InputFormView(
    private val contextCompletionService: CompletionService,
    private val lexicoCompletionService: CompletionService,
    private val tokenCompletionService: CompletionService,
    private val lang: LangDAO,
    private val activeContextMenus: MutableMap<TextField, ContextMenu> = mutableMapOf()
) : View() {

    val tokenInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = lang.getMessage("prompt_token")
    }
    val synonymInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = lang.getMessage("prompt_synonym")
    }
    val definitionInput: TextArea = textarea {
        addClass(ViewStyles.customTextField)
        promptText = lang.getMessage("prompt_definition")
        prefHeight = 145.0
    }
    val primaryContextInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = lang.getMessage("prompt_primary_context")
    }
    val antonymInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = lang.getMessage("prompt_antonym")
    }
    val secondaryContextInput: TextField = textfield {
        addClass(ViewStyles.customTextField)
        promptText = lang.getMessage("prompt_secondary_context")
    }

    init {
        primaryContextInput.textProperty().addListener { _, _, _ ->
            ViewUtilities.updateAutoCompletion(primaryContextInput, contextCompletionService, activeContextMenus)
        }
        secondaryContextInput.textProperty().addListener { _, _, _ ->
            ViewUtilities.updateAutoCompletion(secondaryContextInput, contextCompletionService, activeContextMenus)
        }
        synonymInput.textProperty().addListener { _, _, _ ->
            ViewUtilities.updateAutoCompletion(synonymInput, lexicoCompletionService, activeContextMenus)
        }
        antonymInput.textProperty().addListener { _, _, _ ->
            ViewUtilities.updateAutoCompletion(antonymInput, lexicoCompletionService, activeContextMenus)
        }
        tokenInput.textProperty().addListener { _, _, _ ->
            ViewUtilities.updateAutoCompletion(tokenInput, tokenCompletionService, activeContextMenus)
        }
    }

    override val root = hbox(10.0) {
        form {
            fieldset {
                field {
                    vbox {
                        label(lang.getMessage("token_label") + lang.getMessage("required_field"))
                        this += tokenInput
                    }
                }
                field {
                    vbox {
                        label(lang.getMessage("primary_context_label") + lang.getMessage("required_field"))
                        this += primaryContextInput
                    }
                }
                field {
                    vbox {
                        label(lang.getMessage("secondary_context_label"))
                        this += secondaryContextInput
                    }
                }
            }
        }
        form {
            fieldset {
                field {
                    vbox {
                        label(lang.getMessage("synonym_label"))
                        this += synonymInput
                    }
                }
                field {
                    vbox {
                        label(lang.getMessage("antonym_label"))
                        this += antonymInput
                    }
                }
            }
        }
        form {
            fieldset {
                field {
                    vbox {
                        label(lang.getMessage("definition_label"))
                        this += definitionInput
                    }
                }
            }
        }
    }
}
