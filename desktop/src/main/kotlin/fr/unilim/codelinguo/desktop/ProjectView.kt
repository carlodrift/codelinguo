package fr.unilim.codelinguo.desktop

import fr.unilim.codelinguo.common.model.Glossary
import fr.unilim.codelinguo.common.persistence.lang.JsonLangDao
import fr.unilim.codelinguo.common.persistence.lang.LangDAO
import fr.unilim.codelinguo.common.persistence.project.JsonProjectDao
import fr.unilim.codelinguo.common.persistence.project.ProjectDao
import fr.unilim.codelinguo.desktop.style.ViewStyles
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*


class ProjectView : View() {
    private val projectDao: ProjectDao = JsonProjectDao()
    private var projectsList = mutableListOf<Glossary>().asObservable()
    private val lang: LangDAO = JsonLangDao()

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
        projectDao.delete("Démo")
        projectsList.clear()
        projectsList.addAll(projectDao.retrieve())

        val comparator = compareBy<Glossary> { it.name == "Démo" }.thenBy { it.name }
        projectsList.sortWith(comparator)
    }


    override val root = vbox {
        primaryStage.width = 550.0
        primaryStage.height = 310.0

        vbox {
            paddingAll = 10.0
            button(lang.getMessage("button_quit")).apply {
                addClass(ViewStyles.helpButton)
                action { Platform.exit() }
            }
        }.alignment = Pos.TOP_RIGHT

        separator { addClass(ViewStyles.separator) }

        hbox(30.0) {
            vbox {
                alignment = Pos.CENTER_LEFT
                label(lang.getMessage("all_projects")) {
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

                                button(lang.getMessage("button_open")).apply {
                                    addClass(ViewStyles.openButton)
                                    action {
                                        openProject(project.name)
                                    }
                                }

                                if (!project.isDemo && project.name != "Démo") {
                                    button(lang.getMessage("button_X")).apply {
                                        addClass(ViewStyles.removeButton)
                                        cursor = Cursor.HAND
                                        action {
                                            confirm(
                                                lang.getMessage("confirm_delete"),
                                                lang.getMessage("confirm_delete_content") + " ${project.name} ?"
                                            ) {
                                                projectDao.delete(project.name)
                                                loadProjects()
                                            }
                                        }
                                    }
                                } else {
                                    button("   ").apply {
                                        addClass(ViewStyles.removeButton)
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

                button(lang.getMessage("button_new_project")).apply {
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
