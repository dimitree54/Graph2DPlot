package we.rashchenko.neurons

import we.rashchenko.utils.Feedback


// @todo maybe make it sequence?
interface NeuronsSampler {
	val name: String
	fun next(): Neuron
	fun reportFeedback(neuron: Neuron, feedback: Feedback)
	fun reportDeath(neuron: Neuron)
}
