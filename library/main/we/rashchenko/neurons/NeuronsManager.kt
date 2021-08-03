package we.rashchenko.neurons

import we.rashchenko.utils.ExponentialMovingAverage
import we.rashchenko.utils.Feedback
import we.rashchenko.utils.softmax
import we.rashchenko.utils.update
import java.util.*

class NeuronsManager : NeuronsSampler {
	override val name: String = "manager"
	private val neuronSamplerMap = mutableMapOf<Neuron, NeuronsSampler>()
	private val samplersScore = mutableMapOf<NeuronsSampler, ExponentialMovingAverage>()
	private val probabilityRanges = mutableMapOf<NeuronsSampler, ClosedFloatingPointRange<Double>>()
	private val random = Random()

	private val defaultScore: Feedback = Feedback.NEUTRAL
	fun add(sampler: NeuronsSampler) {
		if (samplersScore.keys.any { it.name == sampler.name }) {
			throw IllegalArgumentException("Sampler with that name already registered at NeuronsManager")
		}
		samplersScore[sampler] = ExponentialMovingAverage(defaultScore.value)
		updateRanges()
	}

	private fun updateRanges() {
		val keys = samplersScore.keys
		val probabilities = softmax(samplersScore.values.map { it.value })

		probabilityRanges.clear()
		var lastMax = 0.0
		keys.mapIndexed { index, neuronsSampler ->
			val newMax = lastMax + probabilities[index]
			probabilityRanges[neuronsSampler] = lastMax..newMax
			lastMax = newMax
		}
	}

	override fun next(): Neuron {
		random.nextDouble().let { randomValue ->
			probabilityRanges.forEach { (sampler, probabilityRange) ->
				if (randomValue in probabilityRange) {
					return sampler.next().also { neuronSamplerMap[it] = sampler }
				}
			}
		}
		throw Exception("no neuron samplers added to manager")
	}

	override fun reportFeedback(neuron: Neuron, feedback: Feedback) {
		val sampler = neuronSamplerMap[neuron] ?: throw IllegalArgumentException("Unknown neuron")
		sampler.reportFeedback(neuron, feedback)
		samplersScore[sampler]?.update(feedback) ?: throw Exception("Invalid manager state")
		updateRanges()
	}

	override fun reportDeath(neuron: Neuron) {
		val sampler = neuronSamplerMap[neuron] ?: throw IllegalArgumentException("Unknown neuron")
		sampler.reportDeath(neuron)
		neuronSamplerMap.remove(neuron)
	}

	fun getSummary() {
		samplersScore.forEach { (sampler, score) ->
			val probability = probabilityRanges[sampler]!!.let { it.endInclusive - it.start }
			println("${sampler.name} has score $score and have $probability probability to be chosen next time")
		}
	}
}