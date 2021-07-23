package we.rashchenko.environments

import we.rashchenko.neurons.ExternallyControlledNeuron
import we.rashchenko.neurons.ExternallyControlledActivity
import we.rashchenko.neurons.StochasticNeuron
import java.util.*

class SimpleEnvironment(private val tickPeriod: Int) : Environment {
	override val externalSignals: Collection<ExternallyControlledActivity> =
		listOf(ExternallyControlledNeuron(StochasticNeuron()), ExternallyControlledNeuron(StochasticNeuron()))

	override var timeStep: Long = 0
		private set

	private val random = Random()
	override fun tick(){
		if (timeStep % tickPeriod == 0L){
			val newValue = random.nextBoolean()
			externalSignals.forEach {
				it.active = newValue
				onSignalUpdate(it, newValue)
			}
		}
		timeStep++
	}

	override fun onSignalUpdate(neuron: ExternallyControlledActivity, newValue: Boolean) { }
}