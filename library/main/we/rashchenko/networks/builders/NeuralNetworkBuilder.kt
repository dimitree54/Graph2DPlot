package we.rashchenko.networks.builders

import we.rashchenko.environments.Environment
import we.rashchenko.neurons.Neuron

interface NeuralNetworkBuilder {
	fun initialise(numberOfNeurons: Int, environment: Environment)
	fun addNeurons(nNewNeurons: Int)
	fun removeNeurons(neuronsToRemove: Collection<Neuron>)
}