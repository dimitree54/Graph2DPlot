package we.rashchenko.networks

import we.rashchenko.feedbacks.getFeedback
import we.rashchenko.feedbacks.update
import we.rashchenko.neurons.BinaryNeuron
import we.rashchenko.utils.ExponentialMovingAverage
import we.rashchenko.utils.randomIds

class StochasticNeuralNetwork: BinaryNeuralNetwork {
	override val externalNeurons = mutableSetOf<BinaryNeuron>()
	override val connections = mutableMapOf<BinaryNeuron, MutableList<BinaryNeuron>>()
	override var timeStep: Int = 0
		private set

	private val neuronIds = mutableMapOf<BinaryNeuron, Int>()
	private val backwardConnections = mutableMapOf<BinaryNeuron, MutableList<BinaryNeuron>>()
	private val neuronFeedbacks = mutableMapOf<BinaryNeuron, ExponentialMovingAverage>()

	override val neurons: Collection<BinaryNeuron> = neuronIds.keys
	override fun getNeuronId(neuron: BinaryNeuron): Int? = neuronIds[neuron]

	override fun add(neuron: BinaryNeuron){
		neuronIds[neuron] = randomIds.take(1).first()  // @todo does it work?
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
	override fun tick(){
		val currentTickNeurons = nextTickNeurons
		nextTickNeurons = externalNeurons.toMutableSet()
		currentTickNeurons.forEach { source->
			connections[source]!!.forEach { receiver->
				touch(source, receiver)
			}
		}
		currentTickNeurons.forEach{
			it.update(neuronFeedbacks[it]!!.getFeedback(), timeStep)
		}
		timeStep++
	}

	override fun touch(source: BinaryNeuron, receiver: BinaryNeuron) {
		val sourceNeuronId = neuronIds[source]!!
		val wasActive = receiver.active
		// @todo can we do not touch and do not update if already active? Consider external receiver case.
		receiver.touch(sourceNeuronId, timeStep)
		neuronFeedbacks[source]!!.update(receiver.getFeedback(sourceNeuronId))
		if (receiver.active && !wasActive){
			nextTickNeurons.add(receiver)
			onNeuronActivation(receiver)
		}
	}

	override fun onNeuronActivation(neuron: BinaryNeuron) {}
}