import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.SingleGraph

object RandomEuclideanGraph {
    fun createGraphWithDynamicStyles(wordOccurrences: Map<String, Int>, wordContexts: Map<String, String?>?) {
        System.setProperty("org.graphstream.ui", "swing")
        val graph: Graph = SingleGraph("ExampleGraph")

        val css = """
            node {
                size-mode: dyn-size;
                fill-mode: dyn-plain;
                stroke-mode: plain;
                stroke-color: black;
                stroke-width: 1px;
            }
        """.trimIndent()
        graph.setAttribute("ui.stylesheet", css)

        wordOccurrences.forEach { (word, count) ->
            val node = graph.addNode(word)
            node.setAttribute("ui.label", word)
            node.setAttribute("ui.size", count)

            val context = wordContexts?.get(word) ?: "default"
            node.setAttribute("ui.class", context)
        }

        graph.display()
    }
}
