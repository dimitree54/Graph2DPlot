package we.rashchenko

import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*
import we.rashchenko.environments.EvaluationEnvironment
import we.rashchenko.environments.SimpleEnvironment
import we.rashchenko.networks.*
import we.rashchenko.networks.builders.NeuralNetworkIn2DBuilder
import we.rashchenko.networks.controllers.ActivityController
import we.rashchenko.networks.controllers.ComplexController
import we.rashchenko.networks.controllers.TimeController
import we.rashchenko.neurons.NeuronsManager
import we.rashchenko.neurons.zoo.*
import we.rashchenko.utils.*

val externalColor = Color.Blue
val colorActive = Color.Green
val colorPassive = Color.Red
val backColor = Color.Black
val connectionColor = Color.Gray


@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
fun main() {
	val evalEnvironment = SimpleEnvironment(100).let {
		EvaluationEnvironment(
			0.001, 300,
			it, setOf(it.externalSignals.last()), lossFn = ::hemmingDistance
		)
	}
	val nn = run {
		val neuronsManager = NeuronsManager().apply {
			add(StochasticNeuronSampler())
			add(HebbianNeuronSampler())
			add(HebbianAngryNeuronSampler())
			add(HebbianHappyNeuronSampler())
		}
		val nnController = ComplexController(
			TimeController(), ActivityController()
		)
		val controlledNN2D = NeuralNetworkIn2DSpace(ControlledNeuralNetwork(nnController, 0.01, 1000))
		val positionSampler = RandomPositionSampler()
		val connectionsSampler = KNearestConnectionSampler(5)
		val nnBuilder = NeuralNetworkIn2DBuilder(
			controlledNN2D, neuronsManager, positionSampler, positionSampler, connectionsSampler
		).apply { initialise(1000, evalEnvironment) }
		EvolvingNeuralNetwork(controlledNN2D, nnBuilder)
	}

	Window {
		val loss = remember { mutableStateOf(0.0) }
		val ticksPerSec = remember { mutableStateOf(0L) }
		val nnRunning = remember { mutableStateOf(false) }
		val visualMode = remember { mutableStateOf(true) }
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
			networkDrawer(
				nn,
				nnState,
				onLongClick = { visualMode.value = false },
				onClick = { onRunClick(nnRunning, coroutineScope, nn, evalEnvironment, visualMode, nnState, loss) })
			Column {
				Text("TPS: ${ticksPerSec.value.toInt()}", color = Color.Yellow)
				Text("Loss: ${"%.${2}f".format(loss.value)}", color = Color.Yellow)
			}
		} else {
			Column {
				Text("TPS: ${ticksPerSec.value.toInt()}")
				Text("Loss: ${"%.${2}f".format(loss.value)}")
				Button(onClick = {
					onRunClick(
						nnRunning,
						coroutineScope,
						nn,
						evalEnvironment,
						visualMode,
						nnState,
						loss
					)
				}) {
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
	environment: EvaluationEnvironment,
	visualMode: MutableState<Boolean>,
	nnState: MutableState<Pair<List<Vector2>, List<Vector2>>>,
	loss: MutableState<Double>
) {
	nnRunning.value = !nnRunning.value
	coroutineScope.launch(context = newSingleThreadContext("NNThread")) {
		while (nnRunning.value) {
			nn.tick()
			environment.tick()
			if (visualMode.value) {
				nnState.value = nn.getActiveAndPassiveCoordinates()
				delay(100)
			}
			loss.value = environment.loss
		}
	}
}
