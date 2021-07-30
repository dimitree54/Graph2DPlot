package we.rashchenko.networks.builders

import we.rashchenko.networks.NeuralNetwork
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.WorstNNeurons

abstract class NeuralNetworkEvolutionBuilder(
	val nn: NeuralNetwork,
	val neuronsSampler: NeuronsSampler
) : NeuralNetworkBuilder {
	fun evolutionStep(neuronsForSelection: Int) {
		val losers = WorstNNeurons(neuronsForSelection)
		nn.neurons.forEach {
			val neuronFeedback = nn.getFeedback(it)
			neuronsSampler.reportFeedback(it, neuronFeedback)
			losers.add(Pair(it, neuronFeedback))
		}
		removeNeurons(losers.map { it.first })
		addNeurons(neuronsForSelection)
	}
}