package we.rashchenko.networks

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import we.rashchenko.feedbacks.getFeedback
import we.rashchenko.feedbacks.update
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.ExponentialMovingAverage
import we.rashchenko.utils.randomIds

class StochasticNeuralNetwork: NeuralNetwork {
	override val externalNeurons = mutableSetOf<Neuron>()
	override val connections = mutableMapOf<Neuron, MutableList<Neuron>>()
	override var timeStep: Long = 0
		private set

	private val neuronIds = mutableMapOf<Neuron, Int>()
	private val backwardConnections = mutableMapOf<Neuron, MutableList<Neuron>>()
	private val neuronFeedbacks = mutableMapOf<Neuron, ExponentialMovingAverage>()

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

	override fun touch(source: Neuron, receiver: Neuron) {
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

	override fun onNeuronActivation(neuron: Neuron) {}

	override var running: Boolean = false
		private set

	@ObsoleteCoroutinesApi
	override suspend fun run(onTick: () -> Unit) {
		coroutineScope{
			launch(context = newSingleThreadContext("EnvironmentThread")) {
				while (running){
					tick()
					onTick()
				}
			}
		}
	}

	override fun pause() {
		running = false
	}
}