package we.rashchenko.networks

import we.rashchenko.neurons.Neuron

interface NeuralNetwork{
	val neurons: Collection<Neuron>
	val externalNeurons: Collection<Neuron>
	val connections: Map<Neuron, Collection<Neuron>>
	val timeStep: Long

	fun add(neuron: Neuron)
	fun remove(neuron: Neuron)
	fun addExternal(neuron: Neuron)
	fun addConnection(fromNeuron: Neuron, toNeuron: Neuron)
	fun getNeuronId(neuron: Neuron): Int?
	fun touch(source: Neuron, receiver: Neuron)
	fun onNeuronActivation(neuron: Neuron)
	fun tick()

	val running: Boolean
	suspend fun run(onTick: ()->Unit)
	fun pause()
}