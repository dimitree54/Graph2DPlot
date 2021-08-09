package we.rashchenko.neurons.zoo

import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.Feedback
import we.rashchenko.utils.clip
import java.util.*

open class StochasticNeuron(private val initWeight: Double, private val lr: Double) : Neuron {
	private val random = Random()
	private val weights = mutableMapOf<Int, Double>()
	private val feedbacks = mutableMapOf<Int, Feedback>()

	private var internalActive: Boolean = false
	override val active: Boolean
		get() = internalActive

	private var activatedOnTimeStep = Long.MIN_VALUE
	private var activatedOnTouchFrom: Int? = null
	override fun touch(sourceId: Int, timeStep: Long): Boolean {
		if (random.nextDouble() < weights.getOrPut(sourceId) { initWeight }) {
			internalActive = true
			activatedOnTimeStep = timeStep
			activatedOnTouchFrom = sourceId
			return true
		}
		return false
	}

	override fun forgetSource(sourceId: Int) {
		weights.remove(sourceId)
		feedbacks.remove(sourceId)
	}

	override fun getFeedback(sourceId: Int): Feedback = feedbacks.getOrDefault(sourceId, Feedback.NEUTRAL)

	override fun update(feedback: Feedback, timeStep: Long) {
		activatedOnTouchFrom?.let {
			feedbacks[it] = feedback
			weights[it] = weights[it]?.plus(lr * feedback.value)?.clip(0.01, 0.99) ?: initWeight
		}
		if (timeStep != activatedOnTimeStep) {
			internalActive = false
			activatedOnTouchFrom = null
		}
	}
}


class StochasticNeuronSampler : NeuronsSampler {
	private val random = Random()
	override val name: String = "StochasticNeuron"
	override fun next(id: Int): Neuron {
		val randomInitWeight = random.nextDouble()
		val randomLr = random.nextDouble() / 5 - 0.1
		return StochasticNeuron(randomInitWeight, randomLr)
	}

	override fun reportFeedback(id: Int, feedback: Feedback) {}
	override fun reportDeath(id: Int) {}
}
