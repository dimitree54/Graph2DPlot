package we.rashchenko.networks

import we.rashchenko.feedbacks.getFeedback
import we.rashchenko.feedbacks.update
import we.rashchenko.neurons.BinaryNeuron
import we.rashchenko.utils.ExponentialMovingAverage
import we.rashchenko.utils.randomIds

class StochasticNeuralNetwork: BinaryNeuralNetwork {
	override val externalNeurons = mutableSetOf<BinaryNeuron>()
	override val connections = mutableMapOf<BinaryNeuron, MutableList<BinaryNeuron>>()
	override var timeStep: Long = 0
		private set

	private val neuronIds = mutableMapOf<BinaryNeuron, Int>()
	private val backwardConnections = mutableMapOf<BinaryNeuron, MutableList<BinaryNeuron>>()
	private val neuronFeedbacks = mutableMapOf<BinaryNeuron, ExponentialMovingAverage>()

	override val neurons: Collection<BinaryNeuron> = neuronIds.keys
	override fun getNeuronId(neuron: BinaryNeuron): Int? = neuronIds[neuron]

	override fun add(neuron: BinaryNeuron){
		neuronIds[neuron] = randomIds.first()
		connections[neuron] = mutableListOf()
		backwardConnections[neuron] = mutableListOf()
		neuronFeedbacks[neuron] = ExponentialMovingAverage(0.0)
	}

	override fun addExternal(neuron: BinaryNeuron) {
		add(neuron)
		externalNeurons.add(neuron)
	}

	override fun addConnection(fromNeuron: BinaryNeuron, toNeuron: BinaryNeuron){
		connections[fromNeuron]!!.add(toNeuron)
		backwardConnections[toNeuron]!!.add(fromNeuron)
	}

	private var nextTickNeurons = mutableSetOf<BinaryNeuron>()
	private val setAddingLock = Object()
	override fun tick(){
		val currentTickNeurons = nextTickNeurons
		nextTickNeurons = mutableSetOf()
		currentTickNeurons.parallelStream().forEach { source->
			connections[source]!!.forEach { receiver->
				touch(source, receiver)
			}
		}
		currentTickNeurons.parallelStream().forEach{
			it.update(neuronFeedbacks[it]!!.getFeedback(), timeStep)
			if (it.active) {
				synchronized(setAddingLock){
					nextTickNeurons.add(it)
				}
			}
		}
		nextTickNeurons.addAll(externalNeurons)
		timeStep++
	}

	override fun touch(source: BinaryNeuron, receiver: BinaryNeuron) {
		synchronized(receiver) {
			val sourceNeuronId = neuronIds[source]!!
			if (receiver !in nextTickNeurons) {
				receiver.touch(sourceNeuronId, timeStep)
				if (receiver.active) {
					val newUpdate = receiver.getFeedback(sourceNeuronId)
					synchronized(setAddingLock) {
						neuronFeedbacks[source]!!.update(newUpdate)
						nextTickNeurons.add(receiver)
					}
					onNeuronActivation(receiver)
				}
			}
		}
	}

	override fun onNeuronActivation(neuron: BinaryNeuron) {}
}