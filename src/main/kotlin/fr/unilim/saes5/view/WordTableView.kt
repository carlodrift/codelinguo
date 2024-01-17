package fr.unilim.saes5.view

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.context.PrimaryContext
import fr.unilim.saes5.model.context.SecondaryContext
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.ObservableList
import javafx.scene.control.Button
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.util.Callback
import tornadofx.*
import java.util.*

class WordTableView(
    private val words: ObservableList<Word>,
    private val myBundle: ResourceBundle,
    name: String
) : View() {
    var wordTableView: TableView<Word> by singleAssign()

    override val root = tableview(words) {
        wordTableView = this
        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        addClass(ViewStyles.customTableView)
        readonlyColumn(myBundle.getString("token_label") + " ⇅", Word::token)
        readonlyColumn(myBundle.getString("definition_label") + " ⇅", Word::definition)

        column(myBundle.getString("primary_context_label") + " ⇅", Word::context).cellFormat { cell ->
            text = cell?.filterIsInstance<PrimaryContext>()?.joinToString { it.word.token ?: "" } ?: ""
        }
        column(myBundle.getString("secondary_context_label") + " ⇅", Word::context).cellFormat { cell ->
            text = cell?.filterIsInstance<SecondaryContext>()?.joinToString { it.word.token ?: "" } ?: ""
        }
        column(myBundle.getString("synonym_label") + " ⇅", Word::synonyms).cellFormat { cell ->
            text = cell?.joinToString { it.token ?: "" } ?: ""
        }
        column(myBundle.getString("antonym_label") + " ⇅", Word::antonyms).cellFormat { cell ->
            text = cell?.joinToString { it.token ?: "" } ?: ""
        }
        val removeColumn = TableColumn<Word, Word>("")
        removeColumn.cellValueFactory = Callback { cellData -> ReadOnlyObjectWrapper(cellData.value) }
        removeColumn.cellFactory = Callback {
            object : TableCell<Word, Word>() {
                private val button = Button("X").apply {
                    addClass(ViewStyles.removeButton)
                    action {
                        val item = tableRow.item
                        item?.let {
                            words.remove(it)
                            ViewUtilities.updateJsonFile(words, name)
                            ViewUtilities.updateCompletionService(name)
                        }
                    }
                }

                override fun updateItem(item: Word?, empty: Boolean) {
                    super.updateItem(item, empty)
                    graphic = if (empty) {
                        null
                    } else {
                        button
                    }
                }
            }
        }
        columns.add(removeColumn)
        prefHeight = 350.0
    }
}
