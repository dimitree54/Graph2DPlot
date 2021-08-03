package we.rashchenko.neurons.zoo

import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.Feedback

open class HebbianHappyNeuron : HebbianNeuron() {
	override fun getFeedback(sourceId: Int): Feedback = Feedback.VERY_POSITIVE
}


class HebbianHappyNeuronSampler : NeuronsSampler {
	override val name: String = "HebbianHappyNeuron"
	override fun next(): Neuron {
		return HebbianHappyNeuron()
	}

	override fun reportFeedback(neuron: Neuron, feedback: Feedback) {}
	override fun reportDeath(neuron: Neuron) {}
}
