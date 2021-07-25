package we.rashchenko.networks

import we.rashchenko.feedbacks.Feedback
import we.rashchenko.feedbacks.controllers.NeuralNetworkController
import we.rashchenko.feedbacks.update
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.ExponentialMovingAverage
import java.util.*

// @todo maybe better to make ControlledNeuralNetwork parent of StochasticNeuralNetwork, not vice versa?
class ControlledNeuralNetwork(
	private val controller: NeuralNetworkController,
	private val auditProbability: Double,
	private val checkControllerFeedbackEveryNTicks: Long
) : StochasticNeuralNetwork() {
	private val random = Random()
	private var controlling: Boolean = false
	override fun tick() {
		controlling = random.nextDouble() < auditProbability
		super.tick()
		// @todo considering that we update rarely, should we increase weight of that feedback?
		if (timeStep % checkControllerFeedbackEveryNTicks == 0L){
			neurons.forEach {
				neuronFeedbacks.getOrPut(it) { ExponentialMovingAverage(0.0) }.update(
					controller.getControllerFeedback(it)
				)
			}
		}
	}

	override fun update(neuron: Neuron, feedback: Feedback, timeStep: Long) {
		if (controlling) {
			controller.controlledUpdate(neuron, feedback, timeStep) {
				neuron.update(feedback, timeStep)
			}
		} else {
			neuron.update(feedback, timeStep)
		}
	}

	override fun touch(neuron: Neuron, sourceNeuronId: Int, timeStep: Long) {
		if (controlling) {
			controller.controlledTouch(neuron, sourceNeuronId, timeStep) {
				neuron.touch(sourceNeuronId, timeStep)
			}
		} else {
			neuron.touch(sourceNeuronId, timeStep)
		}
	}

	override fun getFeedback(neuron: Neuron, sourceNeuronId: Int): Feedback {
		return if (controlling) {
			controller.controlledGetFeedback(neuron, sourceNeuronId) {
				neuron.getFeedback(sourceNeuronId)
			}
		} else {
			neuron.getFeedback(sourceNeuronId)
		}
	}
}