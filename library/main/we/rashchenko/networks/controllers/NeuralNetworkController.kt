package we.rashchenko.networks.controllers

import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.Feedback

interface NeuralNetworkController {
	fun getControllerFeedback(neuron: Neuron): Feedback = Feedback.NEUTRAL

	/**
	 * As controller function supposed to evaluate all neurons (even never active) it may be slow. So instead of
	 * online Feedback (as in Neuron.getFeedback) it probably returns some aggregated feedback (for example using
	 * ExponentialMovingAverage). So probably sometimes it make sense to clear that aggregation to start evaluation
	 * from the scratch. For that purpose reset function provided.
	 */
	fun reset()
	fun controlledUpdate(neuron: Neuron, feedback: Feedback, timeStep: Long, fn: () -> Unit) = fn()
	fun controlledTouch(neuron: Neuron, sourceNeuronId: Int, timeStep: Long, fn: () -> Unit) = fn()
	fun controlledGetFeedback(neuron: Neuron, sourceNeuronId: Int, fn: () -> Feedback): Feedback = fn()
	// @todo probably all public functions of neuron should be controlled (so it is not possible to call heavy computations there)
	// @todo controller functions will be called from multithreading, consider it
}

