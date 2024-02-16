import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.SingleGraph

object RandomEuclideanGraph {
    fun createGraphWithDynamicStyles(wordOccurrences: Map<String, Int>, wordContexts: Map<String, String?>?) {
        System.setProperty("org.graphstream.ui", "swing")
        val graph: Graph = SingleGraph("ExampleGraph")

        val css = """
            node {
                size-mode: dyn-size;
                fill-mode: gradient-radial;
                fill-color: #36cc32, #36cc32;
                stroke-mode: plain;
                stroke-color: black;
                stroke-width: 2px;
                shadow-offset: 4px, -4px;
                text-mode: normal;
                text-background-mode: rounded-box;
                text-background-color: #FFFFFF;
                text-padding: 5px, 4px;
                text-offset: 0px, 6px;
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

        val maxSize = 125.0

        val contextsAdded = mutableSetOf<String>()
        wordContexts?.values?.distinct()?.forEach { context ->
            if (!contextsAdded.contains(context)) {
                val contextNode = graph.addNode(context)
                contextNode.setAttribute("ui.label", context)
                contextNode.setAttribute("ui.class", "important")
                contextNode.setAttribute("ui.size", maxSize)
                if (context != null) {
                    contextsAdded.add(context)
                }
            }
        }

        wordOccurrences.forEach { (word, count) ->
            val wordNode = graph.addNode(word)
            wordNode.setAttribute("ui.label", word)
            wordNode.setAttribute("ui.size", calculateNodeSize(count, wordOccurrences))

            wordContexts?.get(word)?.let { context ->
                if (contextsAdded.contains(context)) {
                    val edgeId = "$word-$context"
                    if (graph.getEdge(edgeId) == null) {
                        graph.addEdge(edgeId, word, context)
                    }
                }
            }
        }

        graph.display()
    }

    private fun calculateNodeSize(count: Int, wordOccurrences: Map<String, Int>): Double {
        val maxOccurrence = wordOccurrences.values.maxOrNull()?.toDouble() ?: 1.0
        val minOccurrence = wordOccurrences.values.minOrNull()?.toDouble() ?: 1.0
        val maxSize = 125.0
        val minSize = 20.0
        val sizeRange = maxSize - minSize
        val occurrenceRange = maxOccurrence - minOccurrence
        return if (occurrenceRange > 0) {
            minSize + (((count - minOccurrence) / occurrenceRange) * sizeRange)
        } else {
            minSize
        }
    }
}
