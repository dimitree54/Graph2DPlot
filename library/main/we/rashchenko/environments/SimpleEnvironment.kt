package we.rashchenko.environments

import we.rashchenko.neurons.ExternallyControlledNeuron
import we.rashchenko.neurons.zoo.StochasticNeuron
import java.util.*

class SimpleEnvironment(private val tickPeriod: Int) : Environment {
	override val externalSignals: Collection<ExternallyControlledNeuron> =
		listOf(ExternallyControlledNeuron(StochasticNeuron()), ExternallyControlledNeuron(StochasticNeuron()))

	override var timeStep: Long = 0
		private set

	private val random = Random()
	override fun tick() {
		if (timeStep % tickPeriod == 0L) {
			val newValue = random.nextBoolean()
			externalSignals.forEach {
				it.active = newValue
				onSignalUpdate?.invoke(it, newValue)
			}
		}
		timeStep++
	}

	override var onSignalUpdate: ((ExternallyControlledNeuron, Boolean) -> Unit)? = null
}