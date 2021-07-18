package we.rashchenko.neurons

import we.rashchenko.feedbacks.Feedback

class DeterministicExternalBinaryNeuron : ExternalBinaryNeuron {
	private val feedbacks = mutableMapOf<Int, Feedback>()
	override fun touch(sourceId: Int, timeStep: Int) {
		feedbacks[sourceId] = if (active) Feedback.VERY_POSITIVE else Feedback.VERY_NEGATIVE
	}

	override fun forgetSource(sourceId: Int) {
		feedbacks.remove(sourceId)
	}

	override fun getFeedback(sourceId: Int): Feedback = feedbacks.getOrDefault(sourceId, Feedback.NEUTRAL)

	override fun update(feedback: Feedback, timeStep: Int) {}

	override var active: Boolean = false
}