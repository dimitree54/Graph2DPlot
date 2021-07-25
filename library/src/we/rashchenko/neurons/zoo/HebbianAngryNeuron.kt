package we.rashchenko.neurons.zoo

import we.rashchenko.feedbacks.Feedback
import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.clip
import java.util.Random

open class HebbianAngryNeuron : HebbianNeuron() {
	override fun getFeedback(sourceId: Int): Feedback = Feedback.VERY_NEGATIVE
}


class HebbianAngryNeuronSampler: NeuronsSampler{
	override val name: String = "HebbianAngryNeuron"
	override fun next(): Neuron {
		return HebbianNeuron()
	}

	override fun reportFeedback(neuron: Neuron, feedback: Feedback) {}
	override fun reportDeath(neuron: Neuron) {}
}
