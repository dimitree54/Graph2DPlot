package we.rashchenko.gui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.jgrapht.Graph
import we.rashchenko.graph.Colored2D
import we.rashchenko.graph.ColoredEdge2D

class NNState(
    val nodes: MutableState<Collection<Colored2D>>, val edges: MutableState<Collection<ColoredEdge2D>>
) {
    fun update(graph: Graph<Colored2D, ColoredEdge2D>) {
        nodes.value = graph.vertexSet()
        edges.value = graph.edgeSet()
            .map { ColoredEdge2D(it.color, graph.getEdgeSource(it).position, graph.getEdgeTarget(it).position) }
    }
}

@Composable
fun createNNState(graph: Graph<Colored2D, ColoredEdge2D>) = NNState(remember { mutableStateOf(emptyList()) },
    remember { mutableStateOf(emptyList()) }).also { it.update(graph) }
