package we.rashchenko.neurons

import we.rashchenko.feedbacks.Feedback

open class DeterministicBinaryNeuron: BinaryNeuron {
	private val weights = mutableMapOf<Int, Double>()
	private val threshold: Double = 0.5
	private var activation: Double = 0.0

	private fun initializeNewWeight(): Double{
		return 0.0
	}

	override val active: Boolean = activation > threshold

	private var lastTimeStep = Int.MIN_VALUE
	override fun touch(sourceId: Int, timeStep: Int) {
		if (lastTimeStep != timeStep){
			activation = 0.0
		}
		activation += weights.getOrPut(sourceId, ::initializeNewWeight)
		lastTimeStep = timeStep
	}

	override fun forgetSource(sourceId: Int) {
		weights.remove(sourceId)
	}

	override fun getFeedback(sourceId: Int): Feedback {
		return Feedback(0.0)
	}

	override fun update(feedback: Feedback, timeStep: Int) {}
}