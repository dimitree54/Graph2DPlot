package we.rashchenko.gui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.badlogic.gdx.math.Vector2
import we.rashchenko.graph.Colored2D
import we.rashchenko.graph.ColoredEdge2D

val backColor = Color.White

fun DrawScope.clear() {
    drawRect(backColor)
}

fun DrawScope.drawEdge(edge: ColoredEdge2D, scale: Vector2) {
    val perpendicularOffsetPx = 1f
    val perpendicular = Vector2(
        edge.to.y - edge.from.y, -(edge.to.x - edge.from.x)
    ).nor().scl(perpendicularOffsetPx)
    drawLine(
        edge.color,
        Offset(edge.from.x * scale.x + perpendicular.x, edge.from.y * scale.y + perpendicular.y),
        Offset(edge.to.x * scale.x + perpendicular.x, edge.to.y * scale.y + perpendicular.y)
    )
}

fun DrawScope.drawEdges(edges: Collection<ColoredEdge2D>, scale: Vector2) = edges.forEach { drawEdge(it, scale) }

fun DrawScope.drawNode(node: Colored2D, scale: Vector2) =
    drawCircle(node.color, 4f, Offset(node.position.x * scale.x, node.position.y * scale.y))

fun DrawScope.drawNodes(nodes: Collection<Colored2D>, scale: Vector2) = nodes.forEach { drawNode(it, scale) }

fun DrawScope.drawNeuralNetwork(
    nnState: NNState
) {
    clear()
    val scale = Vector2(size.width, size.height)
    drawEdges(nnState.edges.value, scale)
    drawNodes(nnState.nodes.value, scale)
}

@Composable
fun nnCanvas(nnState: NNState) = Canvas(modifier = Modifier.fillMaxSize()) {
    drawNeuralNetwork(nnState)
}
