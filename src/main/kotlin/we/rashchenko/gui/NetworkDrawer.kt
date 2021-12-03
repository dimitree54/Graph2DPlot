package we.rashchenko.gui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import we.rashchenko.base.Feedback
import we.rashchenko.networks.ControlledNeuralNetwork
import we.rashchenko.networks.NeuralNetwork
import we.rashchenko.networks.builders.NeuralNetworkIn2DBuilder
import we.rashchenko.utils.Vector2

val inputColor = Color.Blue
val colorActive = Color.Green
val colorPassive = Color.Red
val backColor = Color.White

enum class NeuronsDrawingMode {
	ACTIVITY, FEEDBACK, EXTERNAL_FEEDBACK, INTERNAL_FEEDBACK;

	fun next(): NeuronsDrawingMode {
		return values().first { it.ordinal == (ordinal + 1) % values().size }
	}
}

fun NeuralNetworkIn2DBuilder.getAllPositions(scale: Vector2 = Vector2.ONES): List<Vector2> {
	return neuralNetwork.neuronIDs.map { getPosition(it)!!.scl(scale) }
}

fun NeuralNetwork.getActivePassiveColors(): List<Color> {
	return neuronIDs.map { if (getNeuron(it)!!.active) colorActive else colorPassive }
}

fun NeuralNetworkIn2DBuilder.getInputPositions(scale: Vector2 = Vector2.ONES): List<Vector2> =
	neuralNetwork.inputNeuronIDs.map { getPosition(it)!!.scl(scale) }

fun NeuralNetwork.getNeuronFeedbackColors(): List<Color> {
	return neuronIDs.map { getFeedbackColor(getFeedback(it)!!) }
}

fun ControlledNeuralNetwork.getControllerFeedbackColors(): List<Color> {
	return neuronIDs.map { getFeedbackColor(getExternalFeedback(it)!!) }
}

fun ControlledNeuralNetwork.getCollaborativeFeedbackColors(): List<Color> {
	return neuronIDs.map { getFeedbackColor(getInternalFeedback(it)!!) }
}

fun NeuralNetworkIn2DBuilder.getConnectionsWithColor(scale: Vector2 = Vector2.ONES):
		List<Triple<Vector2, Vector2, Color>> =
	neuralNetwork.connections.map { (source, receivers) ->
		receivers.map { receiver ->
			Triple(
				getPosition(source)!!.scl(scale), getPosition(receiver)!!.scl(scale),
				Color.Gray  // getFeedbackColor(receiver.getFeedback(neuralNetwork.getNeuronId(source)!!))
			)
		}
	}.flatten()

fun getFeedbackColor(feedback: Feedback): Color {
	return Color(0.5f - feedback.value.toFloat() / 2, 0.5f + feedback.value.toFloat() / 2, 0f)
}

fun DrawScope.clear() {
	drawRect(backColor)
}

fun DrawScope.drawNNConnections(connectionsWithColor: List<Triple<Vector2, Vector2, Color>>) {
	connectionsWithColor.forEach { (source, receiver, color) ->
		val perpendicular = Vector2(receiver.y - source.y, -(receiver.x - source.x)).normalize()
		drawLine(
			color,
			Offset(source.x + perpendicular.x, source.y + perpendicular.y),
			Offset(receiver.x + perpendicular.x, receiver.y + perpendicular.y)
		)
	}
}

fun DrawScope.drawInputs(positions: List<Vector2>) {
	positions.forEach { position ->
		drawCircle(inputColor, 8f, Offset(position.x, position.y))
	}
}

fun DrawScope.drawNeurons(positions: List<Vector2>, colors: List<Color>) {
	positions.zip(colors).forEach { (position, color) ->
		drawCircle(color, 4f, Offset(position.x, position.y))
	}
}

fun DrawScope.drawNeuralNetwork(
	connectionsWithColor: List<Triple<Vector2, Vector2, Color>>,
	inputPositions: List<Vector2>,
	positions: List<Vector2>, colors: List<Color>
) {
	clear()
	val scale = Vector2(size.width, size.height)
	drawNNConnections(connectionsWithColor.map { Triple(it.first.scl(scale), it.second.scl(scale), it.third) })
	drawInputs(inputPositions.map { it.scl(scale) })
	drawNeurons(positions.map { it.scl(scale) }, colors)
}

@Composable
fun nnCanvas(nnState: NNState) = Canvas(modifier = Modifier.fillMaxSize()) {
	drawNeuralNetwork(
		nnState.coloredConnections.value,
		nnState.inputPositions.value,
		nnState.positions.value,
		nnState.neuronColors.value
	)
}
