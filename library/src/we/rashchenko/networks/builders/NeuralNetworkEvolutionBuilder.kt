package we.rashchenko.networks.builders

import we.rashchenko.feedbacks.Feedback
import we.rashchenko.networks.NeuralNetwork
import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.BestN

abstract class NeuralNetworkEvolutionBuilder(
	val nn: NeuralNetwork,
	val neuronsSampler: NeuronsSampler
) : NeuralNetworkBuilder {
	fun evolutionStep(neuronsForSelection: Int) {
		val losers = BestN<Pair<Neuron, Feedback>>(neuronsForSelection) { o1, o2 -> -o1.second.compareTo(o2.second) }
		nn.neurons.forEach {
			val neuronFeedback = nn.getFeedback(it)
			neuronsSampler.reportFeedback(it, neuronFeedback)
			losers.add(Pair(it, neuronFeedback))
		}
		removeNeurons(losers.map { it.first })
		addNeurons(neuronsForSelection)
	}
}