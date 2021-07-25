package we.rashchenko.neurons

import we.rashchenko.feedbacks.Feedback

interface NeuronsSampler {
	val name: String
	fun next(): Neuron
	fun reportFeedback(neuron: Neuron, feedback: Feedback)
	fun reportDeath(neuron: Neuron)
}
