package we.rashchenko.networks

import we.rashchenko.networks.builders.NeuralNetworkBuilder
import we.rashchenko.neurons.MirroringNeuron
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.WorstNNeurons
import java.util.*

class Evolution(
	private val builder: NeuralNetworkBuilder,
	private val neuronsForSelection: Int,
	private val warningsBeforeKill: Int,
	private val stepProbability: Double
) {
	private val warnings = mutableMapOf<Neuron, Int>()
	private val random = Random()
	fun step() {
		if (random.nextDouble() > stepProbability) {
			return
		}
		val losers = WorstNNeurons(neuronsForSelection)
		builder.neuralNetwork.neurons.forEach { neuron ->
			val neuronFeedback = builder.neuralNetwork.getFeedback(neuron)!!
			if (neuron is MirroringNeuron){
				builder.neuronsSampler.reportFeedback(neuron.baseNeuron, neuronFeedback)
			}
			else{
				builder.neuronsSampler.reportFeedback(neuron, neuronFeedback)
			}
			losers.add(Pair(neuron, neuronFeedback))
		}
		losers.forEach {
			val newWarningsValue = warnings.getOrDefault(it.first, 0) + 1
			warnings[it.first] = newWarningsValue
			if (newWarningsValue > warningsBeforeKill) {
				if (builder.remove(it.first)) {
					builder.addNeuron()
				} else {
					// warning for the bad neuron
					it.first.update(it.second, builder.neuralNetwork.timeStep)
				}
			}
		}
	}
}