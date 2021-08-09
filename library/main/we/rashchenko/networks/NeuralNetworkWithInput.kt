package we.rashchenko.networks

import we.rashchenko.neurons.InputNeuron

interface NeuralNetworkWithInput : NeuralNetwork {
	val inputNeurons: Collection<InputNeuron>
	fun addInputNeuron(neuron: InputNeuron): Boolean
}