package we.rashchenko.neurons

import we.rashchenko.base.Activity
import we.rashchenko.utils.Feedback


// Other external neurons possible:
//  - in that one external activity is dominating. That is good for receiving always available external activity,
//     by bad for training on sometimes missing activity.
//  - other option is to make internal activity dominating and make external activity only affecting feedback,
//     that is good for training, but how initial activity will appear at network?
class MirroringNeuron(
	private val externalActivity: Activity, private val baseNeuron: Neuron
) : Neuron by baseNeuron {
	override fun update(feedback: Feedback, timeStep: Long) {
		baseNeuron.update(
			getInternalFeedback(),
			timeStep
		)
	}

	private fun getInternalFeedback(): Feedback {
		return if (externalActivity.active == baseNeuron.active) Feedback.VERY_POSITIVE else Feedback.VERY_NEGATIVE
	}

	override val active: Boolean
		get() = externalActivity.active

	fun getMismatch(): Double {
		return if (externalActivity.active == baseNeuron.active) 0.0 else 1.0
	}
}