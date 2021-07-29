package we.rashchenko.networks.controllers

import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.ExponentialMovingAverage
import we.rashchenko.utils.clip
import kotlin.system.measureTimeMillis
import org.apache.commons.math3.stat.StatUtils
import we.rashchenko.utils.Feedback
import kotlin.math.sqrt

class TimeController : NeuralNetworkController {
	override fun reset() {
		averageTimePerNeuron.clear()
	}
	private val averageTimePerNeuron = mutableMapOf<Neuron, ExponentialMovingAverage>()
	private var cachedMean: Double? = null
	private var cachedStd: Double? = null
	override fun getControllerFeedback(neuron: Neuron): Feedback {
		val (mean, std) = if (cachedMean == null || cachedStd == null){
			calcStats().also { cachedMean = it.first; cachedStd = it.second }
		}
		else{
			cachedMean!! to cachedStd!!
		}
		return averageTimePerNeuron[neuron]?.let{
			Feedback(-((it.value - mean) / std).clip(-1.0, 1.0))  // more than average - bad, less - good
		}?: Feedback.NEUTRAL
	}

	private fun calcStats(): Pair<Double, Double>{
		return averageTimePerNeuron.values.map { it.value }.toDoubleArray().let{
			StatUtils.mean(it) to sqrt(StatUtils.variance(it))
		}
	}

	override fun controlledUpdate(neuron: Neuron, feedback: Feedback, timeStep: Long, fn: () -> Unit) {
		cachedMean = null
		cachedStd = null
		averageTimePerNeuron.getOrPut(neuron) { ExponentialMovingAverage(0.0) }.also {
			it.update(measureTimeMillis(fn).toDouble())
		}
	}

	override fun controlledTouch(neuron: Neuron, sourceNeuronId: Int, timeStep: Long, fn: () -> Unit) {
		cachedMean = null
		cachedStd = null
		averageTimePerNeuron.getOrPut(neuron) { ExponentialMovingAverage(0.0) }.also {
			it.update(measureTimeMillis(fn).toDouble())
		}
	}

	override fun controlledGetFeedback(neuron: Neuron, sourceNeuronId: Int, fn: () -> Feedback): Feedback {
		cachedMean = null
		cachedStd = null
		val feedback: Feedback
		averageTimePerNeuron.getOrPut(neuron) { ExponentialMovingAverage(0.0) }.also {
			it.update(measureTimeMillis { feedback = fn() }.toDouble())
		}
		return feedback
	}
}