package we.rashchenko

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*
import we.rashchenko.environments.Environment
import we.rashchenko.environments.SimpleEnvironment
import we.rashchenko.networks.NeuralNetworkIn2DBuilder
import we.rashchenko.networks.NeuralNetworkIn2DSpace
import we.rashchenko.networks.StochasticNeuralNetwork
import we.rashchenko.networks.getActiveAndPassiveCoordinates
import we.rashchenko.neurons.BinaryNeuron
import we.rashchenko.neurons.StochasticBinaryNeuron
import we.rashchenko.utils.Vector2
import we.rashchenko.utils.kNearest
import we.rashchenko.utils.rectangularPositionSampler
import java.util.*
import kotlin.math.max

val externalColor = Color.Blue
val colorActive = Color.Green
val colorPassive = Color.Red
val backColor = Color.Black
val connectionColor = Color.Gray


val targetScreenSize = Vector2(2560f, 1600f)


@ObsoleteCoroutinesApi
fun main() {
	val environment = SimpleEnvironment()
	val nn = NeuralNetworkIn2DSpace(StochasticNeuralNetwork())
	object : NeuralNetworkIn2DBuilder(nn) {
		override fun neuronsWithPositionSampler(): Pair<BinaryNeuron, Vector2> =
			StochasticBinaryNeuron() to rectangularPositionSampler(targetScreenSize)

		override fun environmentInputsPositionSampler(environment: Environment): Collection<Vector2> =
			environment.externalSignals.map { rectangularPositionSampler(targetScreenSize) }

		override fun connectionsSampler(allPositions: Collection<Vector2>): Map<Vector2, Collection<Vector2>> =
			kNearest(5, allPositions)
	}.apply {
		addNeurons(1000)
		addEnvironment(environment)
		addConnections()
	}

	Window {
		val ticksPerSec = remember { mutableStateOf(0L) }
		val nnRunning = remember { mutableStateOf(false) }
		val visualMode = remember { mutableStateOf(false) }
		val nnState = remember { mutableStateOf(Pair(emptyList<Vector2>(), emptyList<Vector2>())) }
		val coroutineScope = rememberCoroutineScope()
		coroutineScope.launch {
			var prevStep: Long
			while (true) {
				prevStep = nn.timeStep
				delay(1000)
				ticksPerSec.value = nn.timeStep - prevStep
			}
		}
		if (visualMode.value) {
			networkDrawer(nn, nnState, onClick = { visualMode.value = false })
			Text("TPS: ${ticksPerSec.value.toInt()}", color = Color.Yellow)
		} else {
			Column {
				Text("TPS: ${ticksPerSec.value.toInt()}")
				Button(onClick = { onRunClick(nnRunning, coroutineScope, nn, visualMode, nnState) }) {
					Text(if (nnRunning.value) "Pause NN" else "Run NN")
				}
				Button(onClick = { visualMode.value = true }) {
					Text("Show NN")
				}
			}
		}
	}
}

@ObsoleteCoroutinesApi
fun onRunClick(
	nnRunning: MutableState<Boolean>,
	coroutineScope: CoroutineScope,
	nn: NeuralNetworkIn2DSpace,
	visualMode: MutableState<Boolean>,
	nnState: MutableState<Pair<List<Vector2>, List<Vector2>>>
) {
	nnRunning.value = !nnRunning.value
	coroutineScope.launch(context = newSingleThreadContext("NNThread")) {
		while (nnRunning.value) {
			nn.tick()
			if (visualMode.value) {
				nnState.value = nn.getActiveAndPassiveCoordinates()
				delay(100)
			}
		}
	}
}
