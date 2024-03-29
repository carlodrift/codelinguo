package fr.unilim.codelinguo.desktop

import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.ui.layout.springbox.implementations.LinLog
import org.graphstream.ui.swing_viewer.SwingViewer
import org.graphstream.ui.swing_viewer.ViewPanel
import org.graphstream.ui.view.Viewer
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.text.Normalizer
import java.util.regex.Pattern
import javax.swing.*

fun normalizeString(input: String): String {
    val normalizedString = Normalizer.normalize(input.trim(), Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(normalizedString).replaceAll("")
}

object RandomEuclideanGraph {
    private lateinit var graph: SingleGraph
    private lateinit var viewer: SwingViewer

    fun createGraphWithDynamicStyles(
        rawWordOccurrences: Map<String, Int>,
        rawWordContexts: Map<String, String?>?,
        rawWordsInGlossary: Set<String?>,
    ) {
        val wordOccurrences = rawWordOccurrences.mapKeys { (key, _) ->
            normalizeString(key)
        }

        val wordContexts = rawWordContexts?.mapKeys { (key, _) ->
            normalizeString(key)
        }

        val wordsInGlossary = rawWordsInGlossary.mapNotNull { it?.let(::normalizeString) }.toSet()

        System.setProperty("org.graphstream.ui", "swing")
        graph = SingleGraph("Graphe")


        val css = """
            node {
                size-mode: dyn-size;
                fill-mode: gradient-radial;
                stroke-mode: plain;
                stroke-color: black;
                stroke-width: 1px;
                shadow-offset: 0px, 0px;
                text-mode: normal;
                text-background-mode: none; 
                text-padding: 5px, 4px;
                text-offset: 0px, 30px;
                text-size: 14px;
            }
            node.context {
                fill-color: #ffae42, #ffae42; 
                size: 30px, 30px;
            }

            node.important {
                fill-color: #1aec4d, #1aec4d;
            }
            
            node.default {
                fill-color: #f6f6f6, #f6f6f6; 
            }
            edge {
                fill-color: #D3D3D3;
                arrow-size: 5px, 6px;
                stroke-mode: plain;
                stroke-width: 2px;
            }
        """.trimIndent()
        graph.setAttribute("ui.stylesheet", css)

        val contextNodes = mutableMapOf<String, org.graphstream.graph.Node>()

        val layout = LinLog(false)
        graph.addSink(layout)
        layout.addAttributeSink(graph)

        graph.addSink(layout)
        layout.addAttributeSink(graph)

        wordContexts?.values?.distinct()?.forEach { rawContext ->
            var context: String? = rawContext
            if (context != null && !contextNodes.containsKey(context)) {
                if (graph.getNode(context) != null) {
                    while (graph.getNode(context) != null) {
                        context += " "
                    }
                }
                val contextNode = graph.addNode(context)
                contextNode.setAttribute("ui.label", context)
                contextNode.setAttribute("ui.class", "context")
                contextNodes[context] = contextNode
            }
        }

        wordOccurrences.forEach { (rawWord, count) ->
            var word: String? = rawWord
            if (graph.getNode(word) != null) {
                while (graph.getNode(word) != null) {
                    word += " "
                }
            }
            val wordNode = graph.addNode(word)
            wordNode.setAttribute("ui.label", word)
            val nodeSize = calculateNodeSize(count, wordOccurrences)
            wordNode.setAttribute("ui.size", nodeSize)
            val textOffsetX = nodeSize / 2 + 5
            wordNode.setAttribute("ui.style", "text-offset: $textOffsetX, 15px;")
            val cssClass = when {
                word in wordsInGlossary -> "important"
                wordContexts?.keys?.contains(word) == true -> "context"
                else -> "default"
            }
            wordNode.setAttribute("ui.class", cssClass)

            wordContexts?.get(word)?.let { context ->
                contextNodes[context]?.let { contextNode ->
                    val edgeId = "$word-$context"
                    if (graph.getEdge(edgeId) == null) {
                        graph.addEdge(edgeId, wordNode, contextNode).setAttribute("layout.weight", 4.0)
                    }
                }
            }
        }

        viewer = SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD)
        viewer.enableAutoLayout()
        val viewPanel = viewer.addDefaultView(false) as ViewPanel
        viewPanel.setFocusable(true)
        viewPanel.requestFocusInWindow()

        val resetButton = JButton("Réinitialiser").apply {
            this.font = Font("Arial", Font.BOLD, 12)
            this.background = Color.WHITE

            addActionListener {
                viewPanel.camera.setViewCenter(0.0, 0.0, 0.0)
                viewPanel.camera.viewPercent = 1.0
            }
        }

        viewPanel.addMouseWheelListener { e ->
            val camera = viewPanel.camera
            val zoomFactor = if (e.wheelRotation < 0) 1.1 else 0.9
            camera.viewPercent *= zoomFactor
        }

        var dragStartPoint: Point? = null
        viewPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON2) {
                    dragStartPoint = e.getPoint()
                    viewPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON2) {
                    dragStartPoint = null
                    viewPanel.setCursor(Cursor.getDefaultCursor())
                }
            }
        })

        viewPanel.addMouseMotionListener(object : java.awt.event.MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (dragStartPoint != null) {
                    val dragEndPoint = e.point
                    val dx = (dragEndPoint.x - dragStartPoint!!.x) * 0.1
                    val dy = (dragEndPoint.y - dragStartPoint!!.y) * 0.1

                    val camera = viewPanel.camera
                    camera.setViewCenter(
                        camera.viewCenter.x - dx * camera.viewPercent,
                        camera.viewCenter.y + dy * camera.viewPercent,
                        0.0
                    )
                    camera.viewPercent = camera.viewPercent

                    dragStartPoint = e.point
                }
            }
        })

        Thread {
            Thread.sleep(5000)
            viewer.disableAutoLayout()
        }.start()



        JFrame("Graphe").apply {
            val legendPanel = JPanel(FlowLayout(FlowLayout.LEADING, 30, 10)).apply {
                border = BorderFactory.createEmptyBorder()
                add(Box.createHorizontalStrut(-30))
                add(JToggleButton("Contexte principal").apply {
                    foreground = Color(255, 174, 66)
                    isSelected = true
                    addActionListener { toggleVisibility("context", isSelected) }
                })
                add(JToggleButton("Termes dans le glossaire").apply {
                    foreground = Color(26, 201, 77)
                    isSelected = true
                    addActionListener { toggleVisibility("important", isSelected) }
                })
                add(JToggleButton("Termes hors glossaire").apply {
                    foreground = Color.BLACK
                    isSelected = true
                    addActionListener { toggleVisibility("default", isSelected) }
                })
            }

            val bottomPanel = JPanel(BorderLayout()).apply {
                add(legendPanel, BorderLayout.WEST)
                add(resetButton, BorderLayout.EAST)
            }

            contentPane.add(viewPanel, BorderLayout.CENTER)
            contentPane.add(bottomPanel, BorderLayout.SOUTH)

            setSize(800, 600)
            setLocationRelativeTo(null)
            isVisible = true
        }
    }

    private fun calculateNodeSize(count: Int, wordOccurrences: Map<String, Int>): Double {
        val maxOccurrence = wordOccurrences.values.maxOrNull()?.toDouble() ?: 1.0
        val minOccurrence = wordOccurrences.values.minOrNull()?.toDouble() ?: 1.0
        val maxSize = 30.0
        val minSize = 10.0
        val sizeRange = maxSize - minSize
        val occurrenceRange = maxOccurrence - minOccurrence
        return if (occurrenceRange > 0) {
            minSize + ((count - minOccurrence) / occurrenceRange) * sizeRange
        } else {
            maxSize
        }
    }

    private fun toggleVisibility(className: String, visible: Boolean) {
        graph.nodes().forEach { node ->
            val nodeClass = node.getAttribute("ui.class")
            if (nodeClass == className) {
                if (visible) {
                    node.removeAttribute("ui.hide")
                } else {
                    node.setAttribute("ui.hide", true)
                }
            }
        }
        updateLayout()
    }

    private fun updateLayout() {
        viewer.disableAutoLayout()
        viewer.enableAutoLayout()
    }

}
