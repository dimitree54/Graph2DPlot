package we.rashchenko.neurons.zoo

import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.Feedback
import java.util.*

open class HebbianNeuron : Neuron {
	private val random = Random()
	private val weights = mutableMapOf<Int, Double>()

	private fun initializeNewWeight(): Double {
		return 0.5
	}

	private var internalActive: Boolean = false
	override val active: Boolean
		get() = internalActive

	override fun touch(sourceId: Int, timeStep: Long): Boolean {
		weights.getOrPut(sourceId, ::initializeNewWeight).let {
			if (random.nextDouble() < it) {
				internalActive = true
				weights[sourceId] = it + 0.01
			} else {
				weights[sourceId] = it - 0.001
			}
		}
		return internalActive
	}

	override fun forgetSource(sourceId: Int) {
		weights.remove(sourceId)
	}

	private var receivedFeedback = Feedback.NEUTRAL
	override fun getFeedback(sourceId: Int): Feedback = receivedFeedback

	override fun update(feedback: Feedback, timeStep: Long) {
		receivedFeedback = feedback
	}
}


class HebbianNeuronSampler : NeuronsSampler {
	override val name: String = "HebbianNeuron"
	override fun next(): Neuron {
		return HebbianNeuron()
	}

	override fun reportFeedback(neuron: Neuron, feedback: Feedback) {}
	override fun reportDeath(neuron: Neuron) {}
}
