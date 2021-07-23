package we.rashchenko.environments

import we.rashchenko.neurons.ExternallyControlledActivity
import we.rashchenko.utils.ExponentialMovingAverage
import java.util.*

class EvaluationEnvironment(
	private val probabilityOfTest: Double,
	private val testLength: Int,
	private val baseEnvironment: Environment,
	private val neuronsToTest: Set<ExternallyControlledActivity>,
	private val lossFn: (Collection<Boolean>, Collection<Boolean>) -> Double
) :
	Environment by baseEnvironment, Evaluable {
	init {
		assert(probabilityOfTest in 0.0..1.0)
		assert(testLength > 0)
		assert(neuronsToTest.all { it in baseEnvironment.externalSignals })
	}
	private val random = Random()
	private val lossAggregator = ExponentialMovingAverage(0.0)
	override val loss: Double
		get() = lossAggregator.value

	private var testMode: Boolean = false
	private var testModeStartTimeStep: Long = 0

	private fun setExternalSignalsControl(value: Boolean) = neuronsToTest.forEach { it.externallyControlled = value }
	private fun startTesting(){
		testMode = true
		testModeStartTimeStep = timeStep
		setExternalSignalsControl(false)
	}
	private fun stopTesting(){
		testMode = false
		setExternalSignalsControl(true)
	}
	private fun maybeSwitchTesting(){
		if (testMode) {
			if (timeStep - testModeStartTimeStep > testLength) {
				stopTesting()
			}
		} else {
			if (random.nextDouble() < probabilityOfTest){
				startTesting()
			}
		}
	}

	val targetActivity = mutableMapOf<ExternallyControlledActivity, Boolean>()
	override fun onSignalUpdate(neuron: ExternallyControlledActivity, newValue: Boolean) {
		if (testMode && neuron in neuronsToTest){
			targetActivity[neuron] = newValue
		}
	}

	override fun tick() {
		maybeSwitchTesting()
		baseEnvironment.tick()
		if (testMode){
			val (target, prediction) = neuronsToTest.map { targetActivity[it]!! to it.active }.unzip()
			lossAggregator.update(lossFn(target, prediction))
		}
	}
}