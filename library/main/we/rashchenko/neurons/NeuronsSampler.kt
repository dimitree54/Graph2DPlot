package we.rashchenko.neurons

import we.rashchenko.utils.Feedback

// @ todo for now sampler can save neuron references and change their behaviour
//    how can we restrict that? Maybe sampler should not sample neurons directly,
//    but sample some init info for them, so neuron can be built outside of sampler?
interface NeuronsSampler {
	val name: String
	fun next(): Neuron
	fun reportFeedback(neuron: Neuron, feedback: Feedback)
	fun reportDeath(neuron: Neuron)
}
