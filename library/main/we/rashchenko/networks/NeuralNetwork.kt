package we.rashchenko.networks

import we.rashchenko.base.Ticking
import we.rashchenko.neurons.MirroringNeuron
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.Feedback

interface NeuralNetwork : Ticking {
	val neurons: Collection<Neuron>
	val inputNeurons: Collection<MirroringNeuron>
	val connections: Map<Neuron, Collection<Neuron>>

	fun add(neuron: Neuron): Boolean
	fun remove(neuron: Neuron): Boolean
	fun addConnection(fromNeuron: Neuron, toNeuron: Neuron): Boolean
	fun getNeuronId(neuron: Neuron): Int?
	fun getFeedback(neuron: Neuron): Feedback?
}