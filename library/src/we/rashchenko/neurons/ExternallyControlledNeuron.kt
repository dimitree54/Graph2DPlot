package we.rashchenko.neurons

import we.rashchenko.feedbacks.Feedback

class ExternallyControlledNeuron(private val baseNeuron: Neuron) : Neuron by baseNeuron, ExternallyControlledActivity {
	override fun update(feedback: Feedback, timeStep: Long) {
		if (externallyControlled) {
			baseNeuron.update(getInternalFeedback(), timeStep)
		} else {
			baseNeuron.update(feedback, timeStep)
		}
	}

	private val feedbacks = mutableMapOf<Int, Feedback>()
	override fun forgetSource(sourceId: Int) {
		baseNeuron.forgetSource(sourceId)
		feedbacks.remove(sourceId)
	}

	override fun getFeedback(sourceId: Int): Feedback {
		return if (externallyControlled && sourceId in feedbacks) {
			feedbacks[sourceId]!!
		} else {
			baseNeuron.getFeedback(sourceId)
		}
	}

	override fun touch(sourceId: Int, timeStep: Long) {
		baseNeuron.touch(sourceId, timeStep)
		if (externallyControlled) {
			feedbacks[sourceId] = if (active) Feedback.VERY_POSITIVE else Feedback.VERY_NEGATIVE
		}
	}

	private fun getInternalFeedback(): Feedback =
		if (active == baseNeuron.active) Feedback.VERY_POSITIVE else Feedback.VERY_NEGATIVE

	override var externallyControlled: Boolean = true
	private var externalActive: Boolean = false
	override var active: Boolean
		get() = if (externallyControlled) externalActive else baseNeuron.active
		set(value) {
			externalActive = value
		}
}