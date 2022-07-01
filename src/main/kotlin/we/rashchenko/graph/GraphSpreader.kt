package we.rashchenko.graph

import com.badlogic.gdx.math.Vector2
import org.jgrapht.Graph
import kotlin.math.log10

private const val optimalDistance = 0.05f
private const val learningRate = 0.001f
private const val epsilon = 0.0001f

private fun getForce(distance: Float) = log10(distance / optimalDistance + epsilon)

private fun Vector2.fitInSquare() = set(x.coerceIn(0f, 1f), y.coerceIn(0f, 1f))

private fun getShift(vertex: Vector2, neighbours: Collection<Vector2>): Vector2 {
    val shift = Vector2()
    neighbours.forEach { neighbour ->
        val edge = Vector2(neighbour).sub(vertex)
        val force = getForce(edge.len()) * learningRate / neighbours.size
        shift.add(edge.nor().scl(force))
    }
    return shift
}

fun <T> Graph<Positioned2D, T>.spread(locked: Set<Positioned2D> = emptySet()) {
    this.vertexSet().filter{it !in locked}.forEach { vertex ->
        val neighbours =
            incomingEdgesOf(vertex).map { getEdgeSource(it).position } + outgoingEdgesOf(vertex).map { getEdgeTarget(it).position }
        val shift = getShift(vertex.position, neighbours)
        vertex.position.add(shift).fitInSquare()
    }
}