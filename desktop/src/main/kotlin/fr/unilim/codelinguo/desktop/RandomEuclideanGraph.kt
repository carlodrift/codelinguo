import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.ui.layout.springbox.implementations.LinLog
import org.graphstream.ui.swing_viewer.SwingViewer
import org.graphstream.ui.swing_viewer.ViewPanel
import org.graphstream.ui.view.Viewer
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


object RandomEuclideanGraph {
    fun createGraphWithDynamicStyles(
        wordOccurrences: Map<String, Int>,
        wordContexts: Map<String, String?>?,
        wordsInGlossary: Set<String?>
    ) {
        System.setProperty("org.graphstream.ui", "swing")
        val graph = SingleGraph("ExampleGraph")


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

        wordContexts?.values?.distinct()?.forEach { context ->
            if (context != null && !contextNodes.containsKey(context)) {
                val contextNode = graph.addNode(context)
                contextNode.setAttribute("ui.label", context)
                contextNode.setAttribute("ui.class", "context")
                contextNodes[context] = contextNode
            }
        }

        wordOccurrences.forEach { (word, count) ->
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
                    graph.addEdge(edgeId, wordNode, contextNode).setAttribute("layout.weight", 4.0)
                }
            }
        }

        val viewer = SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD)
        viewer.enableAutoLayout()
        val viewPanel = viewer.addDefaultView(false) as ViewPanel
        viewPanel.setFocusable(true)
        viewPanel.requestFocusInWindow()

        val resetButton = JButton("Reset View").apply {
            this.font = Font("Arial", Font.BOLD, 12)
            this.background = Color.WHITE

            addActionListener {
                viewPanel.camera.setViewCenter(0.0, 0.0, 0.0)
                viewPanel.camera.viewPercent = 1.0
            }
        }


        viewPanel.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                val moveDelta = 0.1
                val camera = viewPanel.camera
                val center = camera.viewCenter

                when (e.keyCode) {
                    KeyEvent.VK_LEFT ->
                        camera.setViewCenter(center.x - moveDelta, center.y, center.z)
                    KeyEvent.VK_RIGHT ->
                        camera.setViewCenter(center.x + moveDelta, center.y, center.z)
                    KeyEvent.VK_UP ->
                        camera.setViewCenter(center.x, center.y + moveDelta, center.z)
                    KeyEvent.VK_DOWN ->
                        camera.setViewCenter(center.x, center.y - moveDelta, center.z)
                }
            }
        })

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
                    camera.setViewCenter(camera.viewCenter.x - dx * camera.viewPercent, camera.viewCenter.y + dy * camera.viewPercent, 0.0)
                    camera.viewPercent = camera.viewPercent

                    dragStartPoint = e.point
                }
            }
        })


        Thread {
            Thread.sleep(5000)
            viewer.disableAutoLayout()
        }.start()



        JFrame("Graph Frame").apply {
            val legendPanel = JPanel(FlowLayout(FlowLayout.LEADING, 30, 10)).apply {
                border = BorderFactory.createEmptyBorder()
                add(Box.createHorizontalStrut(-30))
                add(JLabel("Contexte principal").apply { foreground = Color(255,174,66) })
                add(JLabel("Termes du code correspondant au glossaire").apply { foreground = Color(26, 201, 77) })
                add(JLabel("Termes du Code").apply { foreground = Color.BLACK })
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
}
