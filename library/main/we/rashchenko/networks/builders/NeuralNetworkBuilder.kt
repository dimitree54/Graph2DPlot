package we.rashchenko.networks.builders

import we.rashchenko.networks.NeuralNetwork
import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler

interface NeuralNetworkBuilder {
	val neuralNetwork: NeuralNetwork
	val neuronsSampler: NeuronsSampler
	fun addNeuron(): Neuron
	fun remove(neuronToRemove: Neuron): Boolean
}