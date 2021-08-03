package we.rashchenko.networks.controllers

import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.Feedback

class ComplexController(private vararg val controllers: NeuralNetworkController) : NeuralNetworkController {
	override fun reset() {
		controllers.forEach { it.reset() }
	}

	override fun getControllerFeedback(neuron: Neuron): Feedback {
		return Feedback(controllers.sumOf { it.getControllerFeedback(neuron).value } / controllers.size)
	}

	private fun internalControlledUpdate(
		localControllers: List<NeuralNetworkController>,
		neuron: Neuron, feedback: Feedback, timeStep: Long, fn: () -> Unit
	) {
		if (localControllers.size == 1) {
			localControllers.first().controlledUpdate(neuron, feedback, timeStep, fn)
		}
		localControllers.last().controlledUpdate(neuron, feedback, timeStep) {
			internalControlledUpdate(localControllers.take(localControllers.size - 1), neuron, feedback, timeStep, fn)
		}
	}

	override fun controlledUpdate(neuron: Neuron, feedback: Feedback, timeStep: Long, fn: () -> Unit) {
		internalControlledUpdate(controllers.toList(), neuron, feedback, timeStep, fn)
	}

	private fun internalControlledTouch(
		localControllers: List<NeuralNetworkController>,
		neuron: Neuron, sourceNeuronId: Int, timeStep: Long, fn: () -> Unit
	) {
		if (localControllers.size == 1) {
			localControllers.first().controlledTouch(neuron, sourceNeuronId, timeStep, fn)
		}
		localControllers.last().controlledTouch(neuron, sourceNeuronId, timeStep) {
			internalControlledTouch(
				localControllers.take(localControllers.size - 1),
				neuron,
				sourceNeuronId,
				timeStep,
				fn
			)
		}
	}

	override fun controlledTouch(neuron: Neuron, sourceNeuronId: Int, timeStep: Long, fn: () -> Unit) {
		internalControlledTouch(controllers.toList(), neuron, sourceNeuronId, timeStep, fn)
	}

	private fun internalControlledGetFeedback(
		localControllers: List<NeuralNetworkController>,
		neuron: Neuron, sourceNeuronId: Int, fn: () -> Feedback
	): Feedback {
		if (localControllers.size == 1) {
			return localControllers.first().controlledGetFeedback(neuron, sourceNeuronId, fn)
		}
		return localControllers.last().controlledGetFeedback(neuron, sourceNeuronId) {
			internalControlledGetFeedback(localControllers.take(localControllers.size - 1), neuron, sourceNeuronId, fn)
		}
	}

	override fun controlledGetFeedback(neuron: Neuron, sourceNeuronId: Int, fn: () -> Feedback): Feedback {
		return internalControlledGetFeedback(controllers.toList(), neuron, sourceNeuronId, fn)
	}
}