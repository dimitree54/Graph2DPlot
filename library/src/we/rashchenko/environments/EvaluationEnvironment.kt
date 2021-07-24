package we.rashchenko.environments

import we.rashchenko.neurons.ExternallyControlledNeuron
import we.rashchenko.utils.ExponentialMovingAverage
import java.util.*

class EvaluationEnvironment(
	private val probabilityOfTest: Double,
	private val testLength: Int,
	private val baseEnvironment: Environment,
	private val neuronsToTest: Set<ExternallyControlledNeuron>,
	private val lossFn: (List<Boolean>, List<Boolean>) -> Double
) :
	Environment by baseEnvironment, Evaluable {
	init {
		baseEnvironment.onSignalUpdate = this::onSignalUpdate
	}
	private val random = Random()
	private val lossAggregator = ExponentialMovingAverage(0.0)
	override val loss: Double
		get() = lossAggregator.value

	private var testMode: Boolean = false
	private var testModeStartTimeStep: Long = 0

	private fun setExternalSignalsControl(value: Boolean) = neuronsToTest.forEach { it.externallyControlled = value }
	private fun startTesting() {
		testMode = true
		testModeStartTimeStep = timeStep
		setExternalSignalsControl(false)
	}

	private fun stopTesting() {
		testMode = false
		setExternalSignalsControl(true)
	}

	private fun maybeSwitchTesting() {
		if (testMode) {
			if (timeStep - testModeStartTimeStep > testLength) {
				stopTesting()
			}
		} else {
			if (random.nextDouble() < probabilityOfTest) {
				startTesting()
			}
		}
	}

	private val targetActivity = mutableMapOf<ExternallyControlledNeuron, Boolean>()
	private fun onSignalUpdate(neuron: ExternallyControlledNeuron, newValue: Boolean) {
		if (neuron in neuronsToTest) {
			targetActivity[neuron] = newValue
		}
	}

	override fun tick() {
		maybeSwitchTesting()
		baseEnvironment.tick()
		if (testMode) {
			val (target, prediction) = neuronsToTest.mapNotNull {
				targetActivity[it]?.let { targetActive -> targetActive to it.active } }.unzip()
			lossAggregator.update(lossFn(target, prediction))
		}
	}
}