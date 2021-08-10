package we.rashchenko

import androidx.compose.desktop.Window
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.timeNowMillis
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*
import we.rashchenko.environments.SimpleEnvironment
import we.rashchenko.networks.*
import we.rashchenko.networks.builders.NeuralNetworkIn2DBuilder
import we.rashchenko.networks.controllers.ActivityController
import we.rashchenko.networks.controllers.ComplexController
import we.rashchenko.networks.controllers.TimeController
import we.rashchenko.neurons.InputNeuron
import we.rashchenko.neurons.NeuronsManager
import we.rashchenko.neurons.zoo.HebbianAngryNeuronSampler
import we.rashchenko.neurons.zoo.HebbianHappyNeuronSampler
import we.rashchenko.neurons.zoo.HebbianNeuronSampler
import we.rashchenko.neurons.zoo.StochasticNeuronSampler
import we.rashchenko.utils.ExponentialMovingAverage
import we.rashchenko.utils.Vector2

fun main() {
	val environment = SimpleEnvironment(100)
	val nnWithInput = StochasticNeuralNetwork()
	val controlledNN = ControlledNeuralNetwork(
		nnWithInput,
		ComplexController(
			TimeController(), ActivityController()
		),
		0.01, 1000, 0.2
	)
	val neuronsManager = NeuronsManager().apply {
		add(StochasticNeuronSampler())
		add(HebbianNeuronSampler())
		add(HebbianAngryNeuronSampler())
		add(HebbianHappyNeuronSampler())
	}
	val builder = NeuralNetworkIn2DBuilder(
		controlledNN,
		neuronsManager
	).apply { initialise(10000, environment) }
	val evolution = Evolution(builder, 100, 10, 0.01)

	Window {
		val programState = object {
			val nnRunning = remember { mutableStateOf(false) }
			val visualMode = remember { mutableStateOf(false) }
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
					NeuronsDrawingMode.EXTERNAL_FEEDBACK -> nn.getControllerFeedbackColors()
					NeuronsDrawingMode.INTERNAL_FEEDBACK -> nn.getCollaborativeFeedbackColors()
					else -> nn.getActivePassiveColors()
				}
				coloredConnections.value = builder.getConnectionsWithColor()
			}
		}

		val info = object {
			val score = remember { mutableStateOf(0.0) }
			val ticksPerSec = remember { mutableStateOf(0.0) }
			val managerStats = remember { mutableStateOf("") }

			private var lastTimeStep = 0.0
			private var lastTimeMS = timeNowMillis().toDouble()
			private var scoreAggregator = ExponentialMovingAverage(0.0)
			private var tpsAggregator = ExponentialMovingAverage(0.0)
			fun update(nn: NeuralNetworkWithInput, manager: NeuronsManager) {
				scoreAggregator.update(
					nn.inputNeuronIDs.sumOf { (nn.getNeuron(it)!! as InputNeuron).getInternalFeedback().value })
				score.value = scoreAggregator.value
				val currentTime = timeNowMillis().toDouble()
				tpsAggregator.update(1000 * (nn.timeStep - lastTimeStep) / (currentTime - lastTimeMS + 1))
				ticksPerSec.value = tpsAggregator.value
				lastTimeStep = nn.timeStep.toDouble()
				lastTimeMS = currentTime
				managerStats.value = manager.getSummary()
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
			"TPS: ${info.ticksPerSec.value.toInt()}"
		)

		@Composable
		fun infoScore() = Text(
			"Score: ${"%.${2}f".format(info.score.value)}"
		)

		fun CoroutineScope.runNNThread(visualDelay: Long = 100) {
			launch(context = Dispatchers.Default) {
				println("Launching NN thread")
				delay(1000)
				while (true) {
					if (programState.nnRunning.value) {
						controlledNN.tick()
						environment.tick()
						if (programState.visualMode.value) {
							nnMutableState.update(controlledNN, builder)
							delay(visualDelay)
						}
						info.update(nnWithInput, neuronsManager)
						evolution.step()
					} else {
						delay(1000)
					}
				}
			}
		}

		LaunchedEffect(true) {
			nnMutableState.update(controlledNN, builder)
			runNNThread()
		}

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
			infoScore()
			runButton()
			showButton()
			if (programState.visualMode.value) {
				Button(onClick = {
					programState.neuronsMode.value = programState.neuronsMode.value.next()
					if (!programState.nnRunning.value) {
						nnMutableState.update(controlledNN, builder)
					}
				}) { Text(programState.neuronsMode.value.name) }
			} else {
				Text(info.managerStats.value)
			}
		}
	}
}
