package we.rashchenko.networks

import we.rashchenko.neurons.MirroringNeuron
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.*

class StochasticNeuralNetwork : NeuralNetwork {
	private val neuronIds = mutableMapOf<Neuron, Int>()
	override val neurons: Collection<Neuron> = neuronIds.keys
	override val inputNeurons = mutableSetOf<MirroringNeuron>()

	override val connections = mutableMapOf<Neuron, MutableList<Neuron>>()
	private val backwardConnections = mutableMapOf<Neuron, MutableList<Neuron>>()

	private val neuronFeedbacks = mutableMapOf<Neuron, ExponentialMovingAverage>()

	override fun getNeuronId(neuron: Neuron): Int? = neuronIds[neuron]

	override fun add(neuron: Neuron): Boolean {
		if (neuron in neurons) {
			return false
		}
		if (neuron is MirroringNeuron) {
			inputNeurons.add(neuron)
		}
		neuronIds[neuron] = randomIds.next()
		connections[neuron] = mutableListOf()
		backwardConnections[neuron] = mutableListOf()
		neuronFeedbacks[neuron] = ExponentialMovingAverage(0.0)
		return true
	}

	override fun remove(neuron: Neuron): Boolean {
		connections[neuron]?.forEach { it.forgetSource(neuronIds[neuron]!!) } ?: return false
		connections.remove(neuron)
		backwardConnections.remove(neuron)
		neuronFeedbacks.remove(neuron)
		neuronIds.remove(neuron)
		inputNeurons.remove(neuron)
		nextTickNeurons.remove(neuron)
		return true
	}

	override fun addConnection(fromNeuron: Neuron, toNeuron: Neuron): Boolean {
		if (fromNeuron !in neurons || toNeuron !in neurons) {
			return false
		}
		connections[fromNeuron]!!.add(toNeuron)
		backwardConnections[toNeuron]!!.add(fromNeuron)
		return true
	}

	private var nextTickNeurons = mutableSetOf<Neuron>()
	private val setAddingLock = Object()
	override fun tick() {
		val currentTickNeurons = nextTickNeurons
		nextTickNeurons = mutableSetOf()
		currentTickNeurons.parallelStream().forEach { source ->
			connections[source]!!.forEach { receiver ->
				if (source.active) {
					touch(source, receiver)
				} else {
					throw Exception("This should never happen")
				}
			}
		}
		currentTickNeurons.parallelStream().forEach {
			it.update(getFeedback(it)!!, timeStep)
			if (it.active) {
				synchronized(setAddingLock) {
					nextTickNeurons.add(it)
				}
			}
		}
		nextTickNeurons.addAll(inputNeurons.filter { it.active })
		timeStep++
	}

	override var timeStep: Long = 0
		private set

	override fun getFeedback(neuron: Neuron): Feedback? = neuronFeedbacks[neuron]?.getFeedback()

	private fun touch(source: Neuron, receiver: Neuron) {
		synchronized(receiver) {
			val sourceNeuronId = neuronIds[source]!!
			if (receiver !in nextTickNeurons) {
				if (receiver.touch(sourceNeuronId, timeStep)) {
					val newUpdate = receiver.getFeedback(sourceNeuronId)
					synchronized(setAddingLock) {
						neuronFeedbacks[source]!!.update(newUpdate)
						nextTickNeurons.add(receiver)
					}
				}
			}
		}
	}
}