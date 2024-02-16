package fr.unilim.codelinguo.view

import kotlinx.serialization.Serializable
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.graph.*
import kotlin.math.log10

class DynamicGraph(private val data: List<WordContext>) {

    private val graph = SingleGraph("ContextGraph").apply {
        setAttribute("ui.stylesheet", styleSheet())
        setAttribute("ui.quality")
        setAttribute("ui.antialias")
    }

    fun createGraph() {
        data.forEach { wordContext ->
            val node = graph.addNode(wordContext.word.token)
            node.setAttribute("ui.label", wordContext.word.token)
            node.setAttribute("ui.size", 20.0)
            node.setAttribute("ui.style", "fill-color: gray;")

            wordContext.context.forEach { context ->
                graph.addNode(context.word.token).apply {
                    setAttribute("ui.label", context.word.token)
                }
                val edgeId = "${wordContext.word.token}-${context.word.token}"
                if (graph.getEdge(edgeId) == null) {
                    graph.addEdge(edgeId, wordContext.word.token, context.word.token, true)
                }
            }
        }
        updateNodeStyles()
    }

    private fun updateNodeStyles() {
        graph.nodes().forEach { node ->
            val occurrences = node.getAttribute("occurrences") as? Int ?: 1
            node.setAttribute("ui.size", calculateNodeSize(occurrences))
            node.setAttribute("ui.style", "fill-color: ${determineColorBasedOnContext(node)};")
        }
    }

    private fun calculateNodeSize(occurrences: Int): Double {
        return 20.0 + log10(occurrences.toDouble()) * 5
    }

    private fun determineColorBasedOnContext(node: Node): String {
        return if (node.id.startsWith("primary")) "red" else "blue"
    }

    private fun styleSheet(): String {
        return """
            node {
                size-mode: dyn-size; 
                fill-mode: dyn-plain; 
                stroke-mode: plain; 
                stroke-color: black; 
                stroke-width: 1px; 
                text-alignment: above; 
                text-size: 16px; 
            }
            edge {
                shape: cubic-curve; 
            }
        """.trimIndent()
    }

    fun displayGraph() {
        graph.display()
    }
}

data class WordContext(val word: Word, val context: List<Context>)

data class Word(val token: String)
data class Context(val word: Word, val priority: Float)

@Serializable
data class Glossary(val words: List<Word>)