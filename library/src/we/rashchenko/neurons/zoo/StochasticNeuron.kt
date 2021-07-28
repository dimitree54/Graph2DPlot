package we.rashchenko.neurons.zoo

import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.Feedback
import we.rashchenko.utils.clip
import java.util.Random

open class StochasticNeuron : Neuron {
	private val random = Random()
	private val weights = mutableMapOf<Int, Double>()
	private val feedbacks = mutableMapOf<Int, Feedback>()

	private fun initializeNewWeight(): Double {
		return 0.1
	}

	private var internalActive: Boolean = false
	override val active: Boolean
		get() = internalActive

	private var activatedOnTimeStep = Long.MIN_VALUE
	private var activatedOnTouchFrom: Int? = null
	override fun touch(sourceId: Int, timeStep: Long) {
		if (random.nextDouble() < weights.getOrPut(sourceId, ::initializeNewWeight)) {
			internalActive = true
			activatedOnTimeStep = timeStep
			activatedOnTouchFrom = sourceId
		}
	}

	override fun forgetSource(sourceId: Int) {
		weights.remove(sourceId)
		feedbacks.remove(sourceId)
	}

	override fun getFeedback(sourceId: Int): Feedback = feedbacks.getOrDefault(sourceId, Feedback.NEUTRAL)

	override fun update(feedback: Feedback, timeStep: Long) {
		activatedOnTouchFrom?.let {
			feedbacks[it] = feedback
			weights[it] = weights[it]?.plus(+0.01 * feedback.value)?.clip(0.01, 0.99) ?: initializeNewWeight()
		}
		if (timeStep != activatedOnTimeStep) {
			internalActive = false
			activatedOnTouchFrom = null
		}
	}
}


class StochasticNeuronSampler: NeuronsSampler{
	override val name: String = "StochasticNeuron"
	override fun next(): Neuron {
		return StochasticNeuron()
	}

	override fun reportFeedback(neuron: Neuron, feedback: Feedback) {}
	override fun reportDeath(neuron: Neuron) {}
}
