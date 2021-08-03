package we.rashchenko

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import we.rashchenko.networks.NeuralNetworkIn2DSpace
import we.rashchenko.networks.getConnections
import we.rashchenko.networks.getExternalCoordinates
import we.rashchenko.utils.Vector2

fun DrawScope.drawNN(nn2d: NeuralNetworkIn2DSpace) {
	drawRect(backColor)
	val scale = Vector2(size.width, size.height)
	nn2d.getConnections(scale).forEach { (source, receiver) ->
		drawLine(connectionColor, Offset(source.x, source.y), Offset(receiver.x, receiver.y))
	}
	drawPoints(
		nn2d.getExternalCoordinates(scale).map { Offset(it.x, it.y) },
		PointMode.Points, externalColor, strokeWidth = 15f, cap = StrokeCap.Round
	)
}

fun DrawScope.drawNNState(activePositions: Collection<Vector2>, passivePositions: Collection<Vector2>) {
	drawPoints(
		activePositions.map { Offset(it.x, it.y) }, PointMode.Points, colorActive,
		strokeWidth = 5f, cap = StrokeCap.Round
	)
	drawPoints(
		passivePositions.map { Offset(it.x, it.y) }, PointMode.Points, colorPassive,
		strokeWidth = 5f, cap = StrokeCap.Round
	)
}

@Composable
fun networkStateDrawer(activeAndPassiveNeurons: MutableState<Pair<List<Vector2>, List<Vector2>>>, modifier: Modifier) {
	Canvas(modifier = modifier) {
		val scale = Vector2(size.width, size.height)
		drawNNState(activeAndPassiveNeurons.value.first.map { it.scl(scale) },
			activeAndPassiveNeurons.value.second.map { it.scl(scale) })
	}
}

@ExperimentalFoundationApi
@Composable
fun networkDrawer(
	nn: NeuralNetworkIn2DSpace,
	networkState: MutableState<Pair<List<Vector2>, List<Vector2>>>,
	onClick: () -> Unit,
	onLongClick: () -> Unit
) {
	Canvas(modifier = Modifier.fillMaxSize().combinedClickable(onLongClick = onLongClick, onClick = onClick)) {
		drawNN(nn)
	}
	networkStateDrawer(networkState, modifier = Modifier.fillMaxSize())
}