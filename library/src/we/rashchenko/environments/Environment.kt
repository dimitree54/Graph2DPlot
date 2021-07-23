package we.rashchenko.environments

import we.rashchenko.neurons.ExternallyControlledActivity

interface Environment {
	val externalSignals: Collection<ExternallyControlledActivity>
	fun tick()
	val timeStep: Long
	fun onSignalUpdate(neuron: ExternallyControlledActivity, newValue: Boolean)
}