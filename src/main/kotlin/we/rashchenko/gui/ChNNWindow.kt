package we.rashchenko.gui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.Window
import org.jgrapht.Graph
import we.rashchenko.graph.Colored2D
import we.rashchenko.graph.ColoredEdge2D

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun chNNWindow(graph: Graph<Colored2D, ColoredEdge2D>, onTick: () -> Unit, onCloseRequest: () -> Unit) {
    val nnState = createNNState(graph)
    return Window(onCloseRequest = onCloseRequest, onKeyEvent = { keyEvent ->
        if (keyEvent.key == Key.Spacebar) {
            onTick()
            nnState.update(graph)
        }
        true
    }) {
        nnCanvas(nnState)
    }
}