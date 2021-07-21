package we.rashchenko

import androidx.compose.desktop.Window
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import we.rashchenko.environments.Environment
import we.rashchenko.environments.SimpleEnvironment
import we.rashchenko.networks.*
import we.rashchenko.neurons.BinaryNeuron
import we.rashchenko.neurons.StochasticBinaryNeuron
import we.rashchenko.utils.Vector2
import we.rashchenko.utils.kNearest
import java.util.*
import kotlin.math.max

val externalColor = Color.Blue
val colorActive = Color.Green
val colorPassive = Color.Red
val backColor = Color.Black
val connectionColor = Color.Gray

fun DrawScope.drawNN(nn2d: NeuralNetworkIn2DSpace){
	drawRect(backColor)
	val scale = Vector2(size.width, size.height)
	nn2d.getConnections(scale).forEach { (source, receiver) ->
		drawLine(connectionColor, Offset(source.x, source.y), Offset(receiver.x, receiver.y)) }
	drawPoints(nn2d.getExternalCoordinates(scale).map { Offset(it.x, it.y) },
		PointMode.Points, externalColor, strokeWidth = 15f, cap = StrokeCap.Round)
}

fun DrawScope.drawNNState(activePositions: Collection<Vector2>, passivePositions: Collection<Vector2>){
	drawPoints(activePositions.map { Offset(it.x, it.y) }, PointMode.Points, colorActive,
		strokeWidth = 5f, cap = StrokeCap.Round)
	drawPoints(passivePositions.map { Offset(it.x, it.y) }, PointMode.Points, colorPassive,
		strokeWidth = 5f, cap = StrokeCap.Round)
}

@Composable
fun networkStateDrawer(activeAndPassiveNeurons: MutableState<Pair<List<Vector2>, List<Vector2>>>, modifier: Modifier){
	Canvas(modifier = modifier){
		val scale = Vector2(size.width, size.height)
		drawNNState(activeAndPassiveNeurons.value.first.map { it.scl(scale) },
			activeAndPassiveNeurons.value.second.map { it.scl(scale) })
	}
}

val random = Random()
fun rectangularPositionSampler(rectangle: Vector2 = Vector2(2560f, 1600f)): Vector2{
	val maxDim = max(rectangle.x, rectangle.y)
	var x: Float
	var y: Float
	while (true){
		x = random.nextFloat()
		y = random.nextFloat()
		if (x < rectangle.x / maxDim && y < rectangle.y / maxDim){
			break
		}
	}

	return Vector2(x * maxDim / rectangle.x, y * maxDim / rectangle.y)
}

fun main(){
	val environment = SimpleEnvironment()
	val nn = NeuralNetworkIn2DSpace(StochasticNeuralNetwork())
	object: NeuralNetworkIn2DBuilder(nn){
		override fun neuronsWithPositionSampler(): Pair<BinaryNeuron, Vector2> =
			StochasticBinaryNeuron() to rectangularPositionSampler()
		override fun environmentInputsPositionSampler(environment: Environment): Collection<Vector2> =
			environment.externalSignals.map { rectangularPositionSampler() }
		override fun connectionsSampler(allPositions: Collection<Vector2>): Map<Vector2, Collection<Vector2>> =
			kNearest(5, allPositions)
	}.apply {
		addNeurons(1000)
		addEnvironment(environment)
		addConnections()
	}

	runBlocking {
		Window {
			val v = remember { mutableStateOf(Pair(emptyList<Vector2>(), emptyList<Vector2>())) }
			Canvas(modifier = Modifier.fillMaxSize()){
				drawNN(nn)
			}
			networkStateDrawer(v, modifier = Modifier.fillMaxSize())

			val composableScope = rememberCoroutineScope()
			composableScope.launch {
				while (true) {
					nn.tick()
					v.value = nn.getActiveAndPassiveCoordinates()
					delay(100)
				}
			}
		}
	}
}