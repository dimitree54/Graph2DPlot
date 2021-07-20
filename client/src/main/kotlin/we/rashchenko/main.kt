package we.rashchenko

import androidx.compose.desktop.Window
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
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
		PointMode.Points, externalColor, strokeWidth = 15f)
}

fun DrawScope.drawNNState(activePositions: Collection<Vector2>, passivePositions: Collection<Vector2>){
	drawPoints(activePositions.map { Offset(it.x, it.y) }, PointMode.Points, colorActive, strokeWidth = 5f)
	drawPoints(passivePositions.map { Offset(it.x, it.y) }, PointMode.Points, colorPassive, strokeWidth = 5f)
}

@Composable
fun networkStateDrawer(activeAndPassiveNeurons: MutableState<Pair<List<Vector2>, List<Vector2>>>, modifier: Modifier){
	Canvas(modifier = modifier){
		val scale = Vector2(size.width, size.height)
		drawNNState(activeAndPassiveNeurons.value.first.map { it.scl(scale) },
			activeAndPassiveNeurons.value.second.map { it.scl(scale) })
	}
}

fun main(){
	val environment = SimpleEnvironment()
	val nn = NeuralNetworkIn2DSpace(StochasticNeuralNetwork())
	object: NeuralNetworkIn2DBuilder(nn){
		val random = Random()
		override fun neuronsWithPositionSampler(): Pair<BinaryNeuron, Vector2> =
			StochasticBinaryNeuron() to Vector2(random.nextFloat(), random.nextFloat())
		override fun environmentInputsPositionSampler(environment: Environment): Collection<Vector2> =
			environment.externalSignals.map { Vector2(random.nextFloat(), random.nextFloat()) }
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