package we.rashchenko.networks.builders

import we.rashchenko.networks.NeuralNetwork
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.Feedback

interface NeuralNetworkBuilder {
	val neuralNetwork: NeuralNetwork
	fun addNeuron(): Neuron
	fun remove(neuronToRemove: Neuron): Boolean
	fun reportFeedback(neuron: Neuron, feedback: Feedback)
}