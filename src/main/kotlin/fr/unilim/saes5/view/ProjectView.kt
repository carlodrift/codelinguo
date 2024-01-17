package fr.unilim.saes5.view

import fr.unilim.saes5.model.Glossary
import fr.unilim.saes5.persistence.glossary.JsonGlossaryDao
import javafx.geometry.Pos
import javafx.scene.image.Image
import tornadofx.*
import javax.swing.GroupLayout.Alignment

class ProjectView : View() {
    private val glossaryDao = JsonGlossaryDao() // Instance of JsonGlossaryDao
    private var projectsList = mutableListOf<Glossary>().observable()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        projectsList.clear()
        projectsList.addAll(glossaryDao.getAllProjects())
    }

    override val root = vbox(5.0) {
        primaryStage.width = 850.0
        primaryStage.height = 610.0

        vbox {
            paddingAll = 10.0
            button("Fermer X").apply {
                addClass(ViewStyles.downloadButtonHover)
                action { primaryStage.close() }
            }
        }.alignment = Pos.TOP_RIGHT

        separator { addClass(ViewStyles.separator) }

        hbox (5.0){
            vbox {
                alignment = Pos.CENTER_LEFT
                label("Projets récents") {
                    paddingLeft = 5.0
                    addClass(ViewStyles.heading)
                }
                separator { addClass(ViewStyles.separator) }

                scrollpane(fitToWidth = true) {
                    isFitToHeight = true

                    listview(projectsList) {
                        cellFormat { project ->
                            graphic = hbox {
                                label(project.name)
                            }
                        }
                        prefHeight = 90.0
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
