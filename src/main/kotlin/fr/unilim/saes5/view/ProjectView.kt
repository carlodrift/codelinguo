package fr.unilim.saes5.view

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.persistence.glossary.JsonGlossaryDao
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.image.Image
import tornadofx.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority


class ProjectView : View() {
    private val glossaryDao = JsonGlossaryDao()
    private var projectsList = mutableListOf<Glossary>().observable()

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


    private fun loadProjects() {
        projectsList.clear()
        projectsList.addAll(glossaryDao.getAllProjects())
    }


    override val root = vbox {
        primaryStage.width = 550.0
        primaryStage.height = 310.0

        vbox {
            paddingAll = 10.0
            button("Quitter").apply {
                addClass(ViewStyles.helpButton)
                action { Platform.exit() }
            }
        }.alignment = Pos.TOP_RIGHT

        separator { addClass(ViewStyles.separator) }

        hbox (30.0){
            vbox {
                alignment = Pos.CENTER_LEFT
                label("Tous les projets") {
                    padding = Insets(20.0, 0.0, 0.0, 5.0)
                    addClass(ViewStyles.heading)
                }
                separator { addClass(ViewStyles.separator) }

                scrollpane(fitToWidth = true) {
                    isFitToHeight = true

                    listview(projectsList) {
                        cellFormat { project ->
                            graphic = hbox(10.0) {
                                alignment = Pos.CENTER_LEFT

                                label(project.name) {
                                    HBox.setHgrow(this, Priority.ALWAYS)
                                }

                                region {
                                    HBox.setHgrow(this, Priority.ALWAYS)
                                }

                                button("X").apply {
                                    addClass(ViewStyles.removeButton)
                                    action {
                                        confirm("Confirmer la suppression", "Voulez-vous supprimer ${project.name} ?") {
                                            glossaryDao.deleteProject(project.name)
                                            loadProjects()
                                        }
                                    }
                                }
                            }
     
                            setOnMouseClicked {
                                if (it.clickCount == 2) {
                                    primaryStage.close()
                                    find(MainView::class, mapOf(MainView::projectName to project.name)).openWindow()
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

                button("Nouveau projet").apply {
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
