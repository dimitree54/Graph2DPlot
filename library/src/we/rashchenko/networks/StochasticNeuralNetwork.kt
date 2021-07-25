package we.rashchenko.networks

import we.rashchenko.feedbacks.Feedback
import we.rashchenko.feedbacks.getFeedback
import we.rashchenko.feedbacks.update
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.ExponentialMovingAverage
import we.rashchenko.utils.randomIds

open class StochasticNeuralNetwork: NeuralNetwork {
	override val externalNeurons = mutableSetOf<Neuron>()
	override val connections = mutableMapOf<Neuron, MutableList<Neuron>>()
	final override var timeStep: Long = 0
		private set

	private val neuronIds = mutableMapOf<Neuron, Int>()
	private val backwardConnections = mutableMapOf<Neuron, MutableList<Neuron>>()
	protected val neuronFeedbacks = mutableMapOf<Neuron, ExponentialMovingAverage>()

	override val neurons: Collection<Neuron> = neuronIds.keys
	override fun getNeuronId(neuron: Neuron): Int? = neuronIds[neuron]

	override fun add(neuron: Neuron){
		neuronIds[neuron] = randomIds.first()
		connections[neuron] = mutableListOf()
		backwardConnections[neuron] = mutableListOf()
		neuronFeedbacks[neuron] = ExponentialMovingAverage(0.0)
	}

	override fun remove(neuron: Neuron) {
		connections[neuron]?.forEach { it.forgetSource(neuronIds[neuron]!!) }
		connections.remove(neuron)
		backwardConnections.remove(neuron)
		neuronFeedbacks.remove(neuron)
		neuronIds.remove(neuron)
	}

	override fun addExternal(neuron: Neuron) {
		add(neuron)
		externalNeurons.add(neuron)
	}

	override fun addConnection(fromNeuron: Neuron, toNeuron: Neuron){
		connections[fromNeuron]!!.add(toNeuron)
		backwardConnections[toNeuron]!!.add(fromNeuron)
	}

	private var nextTickNeurons = mutableSetOf<Neuron>()
	private val setAddingLock = Object()
	override fun tick(){
		val currentTickNeurons = nextTickNeurons
		nextTickNeurons = mutableSetOf()
		currentTickNeurons.parallelStream().forEach { source->
			connections[source]!!.forEach { receiver->
				if (source.active){
					touch(source, receiver)
				}
				else{
					throw Exception("This should never happen")
				}
			}
		}
		currentTickNeurons.parallelStream().forEach{
			update(it, neuronFeedbacks[it]!!.getFeedback(), timeStep)
			if (it.active) {
				synchronized(setAddingLock){
					nextTickNeurons.add(it)
				}
			}
		}
		nextTickNeurons.addAll(externalNeurons.filter { it.active })
		timeStep++
	}

	private fun touch(source: Neuron, receiver: Neuron) {
		synchronized(receiver) {
			val sourceNeuronId = neuronIds[source]!!
			if (receiver !in nextTickNeurons) {
				touch(receiver, sourceNeuronId, timeStep)
				if (receiver.active) {
					val newUpdate = getFeedback(receiver, sourceNeuronId)
					synchronized(setAddingLock) {
						neuronFeedbacks[source]!!.update(newUpdate)
						nextTickNeurons.add(receiver)
					}
				}
			}
		}
	}

	// neuron based functions moved to protected wrapper fot easy customization in subclasses
	protected open fun update(neuron: Neuron, feedback: Feedback, timeStep: Long){
		neuron.update(feedback, timeStep)
	}
	protected open fun touch(neuron: Neuron, sourceNeuronId: Int, timeStep: Long){
		neuron.touch(sourceNeuronId, timeStep)
	}
	protected open fun getFeedback(neuron: Neuron, sourceNeuronId: Int): Feedback{
		return neuron.getFeedback(sourceNeuronId)
	}
}