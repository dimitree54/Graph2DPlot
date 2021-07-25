package we.rashchenko.feedbacks.controllers

import we.rashchenko.feedbacks.Feedback
import we.rashchenko.neurons.Neuron
import org.apache.commons.math3.stat.StatUtils
import we.rashchenko.utils.clip
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class ActivityController : NeuralNetworkController {
	override fun reset() {
		neuronActivations.clear()
		startTimeStep = Long.MAX_VALUE
		endTimeStep = Long.MIN_VALUE
	}

	private var startTimeStep: Long = Long.MAX_VALUE
	private var endTimeStep: Long = Long.MIN_VALUE
	private val runTime: Long
		get() = if (startTimeStep == Long.MAX_VALUE) 1 else max(1, endTimeStep - startTimeStep)
	private val neuronActivations = mutableMapOf<Neuron, Int>()
	private var cachedMean: Double? = null
	private var cachedStd: Double? = null
	override fun getControllerFeedback(neuron: Neuron): Feedback {
		val (mean, std) = if (cachedMean == null || cachedStd == null){
			calcStats().also { cachedMean = it.first; cachedStd = it.second }
		}
		else{
			cachedMean!! to cachedStd!!
		}
		return Feedback(
			(  // near average activity is good (~1.0), deviation in both sides bad (down to -1.0)
					1 - 2 * abs(neuronActivations[neuron]?.toDouble()?: 0.0 - mean) / std
					).clip(-1.0, 1.0)
		)
	}

	private fun calcStats(): Pair<Double, Double>{
		return neuronActivations.values.map { it.toDouble() / runTime }.toDoubleArray().let{
			StatUtils.mean(it) to sqrt(StatUtils.variance(it))
		}
	}

	override fun controlledUpdate(neuron: Neuron, feedback: Feedback, timeStep: Long, fn: () -> Unit) {
		cachedMean = null
		cachedStd = null
		if (neuron.active){
			neuronActivations[neuron] = neuronActivations.getOrDefault(neuron, 0) + 1
		}
		startTimeStep = min(startTimeStep, timeStep)
		endTimeStep = max(endTimeStep, timeStep)
	}
}