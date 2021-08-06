package we.rashchenko.networks

import we.rashchenko.networks.builders.NeuralNetworkBuilder
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.WorstNNeurons

class Evolution(
	private val builder: NeuralNetworkBuilder,
	private val neuronsForSelection: Int,
	private val warningsBeforeKill: Int
) {
	private val warnings = mutableMapOf<Neuron, Int>()
	fun step() {
		val losers = WorstNNeurons(neuronsForSelection)
		builder.neuralNetwork.neurons.forEach {
			val neuronFeedback = builder.neuralNetwork.getFeedback(it)!!
			builder.neuronsSampler.reportFeedback(it, neuronFeedback)
			losers.add(Pair(it, neuronFeedback))
		}
		losers.forEach {
			val newWarningsValue = warnings.getOrDefault(it.first, 0) + 1
			warnings[it.first] = newWarningsValue
			if (newWarningsValue > warningsBeforeKill) {
				if (builder.remove(it.first)) {
					builder.addNeuron()
				} else {
					// warning
					it.first.update(it.second, builder.neuralNetwork.timeStep)
				}
			}
		}
	}
}