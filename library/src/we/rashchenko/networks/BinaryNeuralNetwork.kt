package we.rashchenko.networks

import we.rashchenko.neurons.BinaryNeuron

interface BinaryNeuralNetwork{
	val neurons: Collection<BinaryNeuron>
	val externalNeurons: Collection<BinaryNeuron>
	val connections: Map<BinaryNeuron, Collection<BinaryNeuron>>
	val timeStep: Long

	fun add(neuron: BinaryNeuron)
	fun remove(neuron: BinaryNeuron)
	fun addExternal(neuron: BinaryNeuron)
	fun addConnection(fromNeuron: BinaryNeuron, toNeuron: BinaryNeuron)
	fun getNeuronId(neuron: BinaryNeuron): Int?
	fun touch(source: BinaryNeuron, receiver: BinaryNeuron)
	fun onNeuronActivation(neuron: BinaryNeuron)
	fun tick()

	val running: Boolean
	suspend fun run(onTick: ()->Unit)
	fun pause()
}