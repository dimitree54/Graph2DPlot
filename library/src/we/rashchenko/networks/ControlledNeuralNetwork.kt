package we.rashchenko.networks

import we.rashchenko.feedbacks.Feedback
import we.rashchenko.feedbacks.controllers.NeuralNetworkController
import we.rashchenko.neurons.Neuron

// @todo maybe better to make ControlledNeuralNetwork parent of StochasticNeuralNetwork, not vice versa?
class ControlledNeuralNetwork(private val controller: NeuralNetworkController): StochasticNeuralNetwork() {
	override fun update(neuron: Neuron, feedback: Feedback, timeStep: Long){
		controller.controlledUpdate(neuron, feedback, timeStep){
			neuron.update(feedback, timeStep)
		}
	}
	override fun touch(neuron: Neuron, sourceNeuronId: Int, timeStep: Long){
		controller.controlledTouch(neuron, sourceNeuronId, timeStep){
			neuron.touch(sourceNeuronId, timeStep)
		}
	}
	override fun getFeedback(neuron: Neuron, sourceNeuronId: Int): Feedback{
		return controller.controlledGetFeedback(neuron, sourceNeuronId){
			neuron.getFeedback(sourceNeuronId)
		}
	}
}