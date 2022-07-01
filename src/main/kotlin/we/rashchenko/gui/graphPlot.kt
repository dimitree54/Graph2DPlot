package we.rashchenko.gui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import org.jgrapht.Graph
import we.rashchenko.graph.Colored
import we.rashchenko.graph.Colored2D

@Composable
fun graphPlot(graph: Graph<Colored2D, Colored>, onTick: () -> Unit, onCloseRequest: () -> Unit) {
    val nnState = createNNState(graph)
    return Window(onCloseRequest = onCloseRequest, onKeyEvent = {
        onTick()
        nnState.update(graph)
        true
    }) {
        nnCanvas(nnState)
    }
}