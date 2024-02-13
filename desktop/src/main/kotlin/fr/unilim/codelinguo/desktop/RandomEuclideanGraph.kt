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
                fill-color: #FFD700, #FF8C00;
                stroke-mode: plain;
                stroke-color: black;
                stroke-width: 2px;
                shadow-mode: gradient-radial;
                shadow-width: 4px;
                shadow-color: #A9A9A9, white;
                shadow-offset: 4px, -4px;
                text-mode: normal;
                text-background-mode: rounded-box;
                text-background-color: #FFFFFF;
                text-padding: 5px, 4px;
                text-offset: 0px, 6px;
                text-size: 14px;
            }
            node.important {
                fill-color: #1E90FF, #00BFFF;
                size: 30px, 30px;
            }
            node.default {
                fill-color: #90EE90, #98FB98;
            }
            edge {
                fill-color: #D3D3D3;
                arrow-size: 5px, 6px;
                stroke-mode: plain;
                stroke-width: 2px;
            }
        """.trimIndent()
        graph.setAttribute("ui.stylesheet", css)

        val maxSize = 200.0
        val minSize = 0.1
        val maxOccurrence = wordOccurrences.values.maxOrNull()?.toDouble() ?: 1.0
        val minOccurrence = wordOccurrences.values.minOrNull()?.toDouble() ?: 1.0
        val sizeRange = maxSize - minSize
        val occurrenceRange = maxOccurrence - minOccurrence

        wordOccurrences.forEach { (word, count) ->
            val node = graph.addNode(word)
            node.setAttribute("ui.label", word)
            val normalizedSize = if (occurrenceRange > 0) {
                minSize + ((count - minOccurrence) / occurrenceRange) * sizeRange
            } else {
                minSize
            }
            node.setAttribute("ui.size", normalizedSize)

            val context = wordContexts?.get(word) ?: "default"
            node.setAttribute("ui.class", context)
        }

        graph.display()
    }
}
