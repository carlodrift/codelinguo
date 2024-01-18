package fr.unilim.saes5.view

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.persistence.glossary.GlossaryDao
import fr.unilim.saes5.persistence.glossary.JsonGlossaryDao
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*
import java.util.*


class ProjectView : View() {
    private val glossaryDao: GlossaryDao = JsonGlossaryDao()
    private var projectsList = mutableListOf<Glossary>().asObservable()
    private val myBundle: ResourceBundle = ResourceBundle.getBundle("Messages", Locale.getDefault())

    init {
        loadProjects()
    }

    override fun onDock() {
        super.onDock()
        loadProjects()
        this.primaryStage.apply {
            height = 350.0
        }
    }

    private fun openProject(projectName: String) {
        primaryStage.close()
        find(MainView::class, mapOf(MainView::projectName to projectName)).openWindow()
    }

    private fun loadProjects() {
        projectsList.clear()
        projectsList.addAll(glossaryDao.getAllProjects())
    }


    override val root = vbox {
        primaryStage.width = 550.0
        primaryStage.height = 310.0

        vbox {
            paddingAll = 10.0
            button(myBundle.getString("button_quit")).apply {
                addClass(ViewStyles.helpButton)
                action { Platform.exit() }
            }
        }.alignment = Pos.TOP_RIGHT

        separator { addClass(ViewStyles.separator) }

        hbox(30.0) {
            vbox {
                alignment = Pos.CENTER_LEFT
                label(myBundle.getString("all_projects")) {
                    padding = Insets(20.0, 0.0, 5.0, 5.0)
                    addClass(ViewStyles.heading)
                }
                separator { addClass(ViewStyles.separator) }

                scrollpane(fitToWidth = true) {

                    listview(projectsList) {
                        selectionModel.selectionMode = SelectionMode.SINGLE

                        isFocusTraversable = false
                        cellFormat { project ->
                            graphic = hbox(10.0) {
                                alignment = Pos.CENTER_LEFT

                                label(project.name.take(20)) {
                                    HBox.setHgrow(this, Priority.ALWAYS)
                                }

                                region {
                                    HBox.setHgrow(this, Priority.ALWAYS)
                                }

                                button(myBundle.getString("button_open")).apply {
                                    addClass(ViewStyles.openButton)
                                    action {
                                        openProject(project.name)
                                    }
                                }

                                button(myBundle.getString("button_X")).apply {
                                    addClass(ViewStyles.removeButton)
                                    cursor = Cursor.HAND
                                    action {
                                        confirm("Confirmer la suppression", "Voulez-vous supprimer ${project.name} ?") {
                                            glossaryDao.deleteProject(project.name)
                                            loadProjects()
                                        }
                                    }
                                }
                            }
                        }
                        prefHeight = 150.0
                        maxHeight = 180.0
                    }
                }

            }

            vbox(10.0) {
                alignment = Pos.CENTER_RIGHT

                button(myBundle.getString("button_new_project")).apply {
                    addClass(ViewStyles.projectButton)
                    graphic = javafx.scene.image.ImageView(Image("/plus.png"))
                    action {
                        find(CreateProjectView::class).openWindow()
                    }
                }
            }

        }.alignment = Pos.CENTER


    }
}
