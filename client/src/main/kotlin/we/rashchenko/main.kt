package we.rashchenko

import androidx.compose.desktop.Window
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import we.rashchenko.environments.Environment
import we.rashchenko.environments.SimpleEnvironment
import we.rashchenko.networks.*
import we.rashchenko.neurons.BinaryNeuron
import we.rashchenko.neurons.StochasticBinaryNeuron
import we.rashchenko.utils.Vector2
import we.rashchenko.utils.kNearest
import java.util.*

fun randomPositionSampler(neuron: BinaryNeuron) = Vector2(random.nextDouble(), random.nextDouble())

val colorActive = Color.Green
val colorPassive = Color.Red

fun DrawScope.drawNN(nn2d: NeuralNetworkIn2DSpace){
	val (activePositions, passivePositions) = nn2d.getActiveAndPassiveCoordinates(Vector2(size.width, size.height))
	drawPoints(activePositions.map { Offset(it.x, it.y) }, PointMode.Points, colorActive, strokeWidth = 3f)
	drawPoints(passivePositions.map { Offset(it.x, it.y) }, PointMode.Points, colorPassive, strokeWidth = 3f)
}

fun main(){
	val environment = SimpleEnvironment()
	val nn = NeuralNetworkIn2DSpace(StochasticNeuralNetwork())
	val builder = object: NeuralNetworkIn2DBuilder(nn){
		val random = Random()
		override fun neuronsWithPositionSampler(): Pair<BinaryNeuron, Vector2> {
			return StochasticBinaryNeuron() to Vector2(random.nextFloat(), random.nextFloat())
		}

		override fun environmentInputsPositionSampler(environment: Environment): Collection<Vector2> {
			return environment.externalSignals.map { Vector2(random.nextFloat(), random.nextFloat()) }
		}

		override fun connectionsSampler(allPositions: Collection<Vector2>): Map<Vector2, Collection<Vector2>> {
			TODO("Not yet implemented")
		}

	}
	Window {
		Canvas(modifier = Modifier.fillMaxSize()) {
			drawNN(nn2d)
		}
	}
}