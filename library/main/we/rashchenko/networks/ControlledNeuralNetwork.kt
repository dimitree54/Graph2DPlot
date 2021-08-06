package we.rashchenko.networks

import we.rashchenko.networks.controllers.NeuralNetworkController
import we.rashchenko.neurons.ControlledNeuron
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.Feedback
import java.util.*

class ControlledNeuralNetwork(
	private val baseNeuralNetwork: NeuralNetwork,
	private val controller: NeuralNetworkController,
	private val auditProbability: Double,
	private val updateControllerFeedbackPeriod: Long,
	private val controllerFeedbackWeight: Double
) : NeuralNetwork by baseNeuralNetwork {
	private val controlledNeurons = mutableMapOf(
		*baseNeuralNetwork.neurons.map { it to ControlledNeuron(it, timeStep) }.toTypedArray()
	)
	private val controllerFeedbacks = mutableMapOf(
		*baseNeuralNetwork.neurons.map { it to Feedback.NEUTRAL }.toTypedArray()
	)

	override fun add(neuron: Neuron): Boolean {
		return baseNeuralNetwork.add(neuron).also { added ->
			if (added) {
				controlledNeurons[neuron] = ControlledNeuron(neuron, timeStep)
				controllerFeedbacks[neuron] = Feedback.NEUTRAL
			}
		}
	}

	override fun remove(neuron: Neuron): Boolean {
		return baseNeuralNetwork.remove(neuron).also { removed ->
			if (removed) {
				controlledNeurons.remove(neuron)
				controllerFeedbacks.remove(neuron)
			}
		}
	}

	private val random = Random()
	override fun tick() {
		if (random.nextDouble() < auditProbability) {
			controlledNeurons.values.forEach { it.control = true }
			baseNeuralNetwork.tick()
			controlledNeurons.values.forEach { it.control = false }
		} else {
			baseNeuralNetwork.tick()
		}
		if (timeStep % updateControllerFeedbackPeriod == 0L) {
			val (neuronsList, controlledNeuronsList) = controlledNeurons.toList().unzip()
			val feedbacks = controller.getControllerFeedbacks(controlledNeuronsList, timeStep)
			neuronsList.indices.forEach { i ->
				controllerFeedbacks[neuronsList[i]] = feedbacks[i]
			}
		}
	}

	override fun getFeedback(neuron: Neuron): Feedback? {
		return baseNeuralNetwork.getFeedback(neuron)?.let {
			Feedback(
				it.value * (1 - controllerFeedbackWeight) +
						controllerFeedbacks[neuron]!!.value * controllerFeedbackWeight
			)
		}
	}
}