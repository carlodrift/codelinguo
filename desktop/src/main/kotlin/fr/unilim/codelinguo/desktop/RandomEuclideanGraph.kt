import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.ui.layout.springbox.implementations.SpringBox

object RandomEuclideanGraph {
    fun createGraphWithDynamicStyles(wordOccurrences: Map<String, Int>, wordContexts: Map<String, String?>?) {
        System.setProperty("org.graphstream.ui", "swing")
        val graph = SingleGraph("ExampleGraph")

        val css = """
            node {
                size-mode: dyn-size;
                fill-mode: gradient-radial;
                fill-color: #36cc32, #36cc32;
                stroke-mode: plain;
                stroke-color: black;
                stroke-width: 2px;
                shadow-mode: plain;
                shadow-color: #A9A9A9;
                shadow-width: 3px;
                shadow-offset: 0px, 0px;
                text-mode: normal;
                text-background-mode: none; 
                text-padding: 5px, 4px;
                text-offset: 0px, 30px;
                text-size: 14px;
            }
            node.important {
                fill-color: #f6f6f6, #f6f6f6;
                size: 30px, 30px;
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

        val layout = SpringBox()
        graph.addSink(layout)
        layout.addAttributeSink(graph)

        Thread {
            layout.compute()
        }.start()


        wordContexts?.values?.distinct()?.forEach { context ->
            if (context != null && !contextNodes.containsKey(context)) {
                val contextNode = graph.addNode(context)
                contextNode.setAttribute("ui.label", context)
                contextNode.setAttribute("ui.class", "important")
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

            wordContexts?.get(word)?.let { context ->
                contextNodes[context]?.let { contextNode ->
                    val edgeId = "$word-$context"
                    graph.addEdge(edgeId, wordNode, contextNode).setAttribute("layout.weight", 4.0)
                }
            }
        }


        graph.display()
    }

    private fun calculateNodeSize(count: Int, wordOccurrences: Map<String, Int>): Double {
        val maxOccurrence = wordOccurrences.values.maxOrNull()?.toDouble() ?: 1.0
        val minOccurrence = wordOccurrences.values.minOrNull()?.toDouble() ?: 1.0
        val maxSize = 20.0
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
