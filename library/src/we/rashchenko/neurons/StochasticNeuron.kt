package we.rashchenko.neurons

import we.rashchenko.feedbacks.Feedback
import java.util.Random

open class StochasticNeuron: Neuron {
	private val random = Random()
	private val weights = mutableMapOf<Int, Double>()

	private fun initializeNewWeight(): Double{
		return 0.1
	}

	private var internalActive: Boolean = false
	override val active: Boolean
		get() = internalActive

	private var activatedOnTimeStep = Long.MIN_VALUE
	override fun touch(sourceId: Int, timeStep: Long) {
		if (random.nextDouble() < weights.getOrPut(sourceId, ::initializeNewWeight)){
			internalActive = true
			activatedOnTimeStep = timeStep
		}
	}

	override fun forgetSource(sourceId: Int) {
		weights.remove(sourceId)
	}

	override fun getFeedback(sourceId: Int): Feedback {
		return Feedback(0.0)
	}

	override fun update(feedback: Feedback, timeStep: Long) {
		if (timeStep != activatedOnTimeStep){
			internalActive = false
		}
	}
}