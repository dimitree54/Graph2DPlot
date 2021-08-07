package we.rashchenko

import androidx.compose.desktop.Window
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.timeNowMillis
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*
import we.rashchenko.environments.Environment
import we.rashchenko.environments.SimpleEnvironment
import we.rashchenko.networks.ControlledNeuralNetwork
import we.rashchenko.networks.Evolution
import we.rashchenko.networks.NeuralNetwork
import we.rashchenko.networks.StochasticNeuralNetwork
import we.rashchenko.networks.builders.NeuralNetworkIn2DBuilder
import we.rashchenko.networks.controllers.ActivityController
import we.rashchenko.networks.controllers.ComplexController
import we.rashchenko.networks.controllers.TimeController
import we.rashchenko.neurons.NeuronsManager
import we.rashchenko.neurons.zoo.HebbianAngryNeuronSampler
import we.rashchenko.neurons.zoo.HebbianHappyNeuronSampler
import we.rashchenko.neurons.zoo.HebbianNeuronSampler
import we.rashchenko.neurons.zoo.StochasticNeuronSampler
import we.rashchenko.utils.ExponentialMovingAverage
import we.rashchenko.utils.Vector2

@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
fun main() {

	val environment = SimpleEnvironment(100)
	val controlledNN = ControlledNeuralNetwork(
		StochasticNeuralNetwork(),
		ComplexController(
			TimeController(), ActivityController()
		),
		0.01, 1000, 0.2
	)
	val builder = NeuralNetworkIn2DBuilder(
		controlledNN,
		NeuronsManager().apply {
			add(StochasticNeuronSampler())
			add(HebbianNeuronSampler())
			add(HebbianAngryNeuronSampler())
			add(HebbianHappyNeuronSampler())
		}
	).apply { initialise(1000, environment) }
	val evolution = Evolution(builder, 100, 10, 0.01)

	Window {
		val programState = object {
			val nnRunning = remember { mutableStateOf(false) }
			val visualMode = remember { mutableStateOf(true) }
			val neuronsMode = remember { mutableStateOf(NeuronsDrawingMode.ACTIVITY) }
		}

		val nnMutableState = object {
			val inputPositions = remember { mutableStateOf(emptyList<Vector2>()) }
			val positions = remember { mutableStateOf(emptyList<Vector2>()) }
			val coloredConnections = remember { mutableStateOf(emptyList<Triple<Vector2, Vector2, Color>>()) }
			val neuronColors = remember { mutableStateOf(emptyList<Color>()) }

			fun update(
				nn: ControlledNeuralNetwork, builder: NeuralNetworkIn2DBuilder
			) {
				inputPositions.value = builder.getInputPositions()
				positions.value = builder.getAllPositions()
				neuronColors.value = when (programState.neuronsMode.value) {
					NeuronsDrawingMode.FEEDBACK -> nn.getNeuronFeedbackColors()
					NeuronsDrawingMode.EXTERNAL_ONLY_FEEDBACK -> nn.getControllerFeedbackColors()
					NeuronsDrawingMode.INTERNAL_ONLY_FEEDBACK -> nn.getCollaborativeFeedbackColors()
					else -> nn.getActivePassiveColors()
				}
				coloredConnections.value = builder.getConnectionsWithColor()
			}
		}

		val info = object {
			val loss = remember { mutableStateOf(0.0) }
			val ticksPerSec = remember { mutableStateOf(0.0) }

			private var lastTimeStep = 0.0
			private var lastTimeMS = timeNowMillis().toDouble()
			private var lossAggregator = ExponentialMovingAverage(0.0)
			fun update(nn: NeuralNetwork) {
				loss.value = nn.inputNeurons.sumOf { it.getMismatch() }
				val currentTime = timeNowMillis().toDouble()
				lossAggregator.update((nn.timeStep - lastTimeStep) / (currentTime - lastTimeMS + 1))
				lastTimeStep = nn.timeStep.toDouble()
				lastTimeMS = currentTime
				loss.value = lossAggregator.value
			}
		}

		@Composable
		fun runButton() = Button(onClick = { programState.nnRunning.value = !programState.nnRunning.value }) {
			Text(if (programState.nnRunning.value) "Pause NN" else "Run NN")
		}

		@Composable
		fun showButton() = Button(onClick = { programState.visualMode.value = !programState.visualMode.value }) {
			Text(if (programState.visualMode.value) "HideNN" else "ShowNN")
		}

		@Composable
		fun infoTPS() = Text(
			"TPS: ${info.ticksPerSec.value.toInt()}",
			color = if (programState.visualMode.value) Color.Yellow else Color.Black
		)

		@Composable
		fun infoLoss() = Text(
			"Loss: ${"%.${2}f".format(info.loss.value)}",
			color = if (programState.visualMode.value) Color.Yellow else Color.Black
		)

		@ObsoleteCoroutinesApi
		fun CoroutineScope.runNNThread(
			environment: Environment,
			nn: ControlledNeuralNetwork,
			builder: NeuralNetworkIn2DBuilder,
			evolution: Evolution,
			visualDelay: Long = 100
		) {
			launch(context = newSingleThreadContext("NNThread")) {
				while (true) {
					if (programState.nnRunning.value) {
						nn.tick()
						environment.tick()
						if (programState.visualMode.value) {
							nnMutableState.update(nn, builder)
							delay(visualDelay)
						}
						info.update(nn)
						evolution.step()
					} else {
						delay(1000)
					}
				}
			}
		}

		rememberCoroutineScope().runNNThread(environment, controlledNN, builder, evolution)
		if (programState.visualMode.value) {
			Canvas(modifier = Modifier.fillMaxSize()) {
				drawNeuralNetwork(
					nnMutableState.coloredConnections.value,
					nnMutableState.inputPositions.value,
					nnMutableState.positions.value,
					nnMutableState.neuronColors.value
				)
			}
		}
		Column {
			infoTPS()
			infoLoss()
			runButton()
			showButton()
			if (programState.visualMode.value) {
				Button(onClick = {
					programState.neuronsMode.value = programState.neuronsMode.value.next()
				}) { Text("Toggle neurons mode") }
			} else {
				// info for non visual mode
			}
		}
	}
}
