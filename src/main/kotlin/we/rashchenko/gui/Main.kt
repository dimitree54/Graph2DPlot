package we.rashchenko.gui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.application
import com.badlogic.gdx.math.Vector2
import org.jgrapht.graph.DefaultDirectedGraph
import we.rashchenko.graph.Colored2D
import we.rashchenko.graph.ColoredEdge2D


fun main() = application {
    val testGraph = DefaultDirectedGraph<Colored2D, ColoredEdge2D>(ColoredEdge2D::class.java)
    val v1 = object : Colored2D {
        override val color = Color.Red
        override val position = Vector2(0.3f, 0.3f)
    }
    val v2 = object : Colored2D {
        override val color = Color.Green
        override val position = Vector2(0.3f, 0.7f)
    }
    val v3 = object : Colored2D {
        override val color = Color.Blue
        override val position = Vector2(0.7f, 0.7f)
    }
    testGraph.addVertex(v1)
    testGraph.addVertex(v2)
    testGraph.addVertex(v3)
    testGraph.addEdge(v1, v2)
    testGraph.addEdge(v1, v3)
    chNNWindow(testGraph, onCloseRequest = ::exitApplication, onTick = {})
}
