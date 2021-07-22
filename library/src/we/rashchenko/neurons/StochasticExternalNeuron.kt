package we.rashchenko.neurons

import we.rashchenko.feedbacks.Feedback

class StochasticExternalNeuron : StochasticNeuron(), ExternalNeuron {
	private val feedbacks = mutableMapOf<Int, Feedback>()
	override fun touch(sourceId: Int, timeStep: Long) {
		super.touch(sourceId, timeStep)
		feedbacks[sourceId] = if (active) Feedback.VERY_POSITIVE else Feedback.VERY_NEGATIVE
	}

	override fun forgetSource(sourceId: Int) {
		super.forgetSource(sourceId)
		feedbacks.remove(sourceId)
	}

	override fun getFeedback(sourceId: Int): Feedback = feedbacks.getOrDefault(sourceId, Feedback.NEUTRAL)

	private fun getInternalFeedback(): Feedback =
		if (active == super.active) Feedback.VERY_POSITIVE else Feedback.VERY_NEGATIVE
	override fun update(feedback: Feedback, timeStep: Long) = super.update(getInternalFeedback(), timeStep)

	private var externalActive = false
	override var active: Boolean
		get() = externalActive
		set(value) {externalActive = value}
}