package we.rashchenko.environments

import we.rashchenko.neurons.ExternallyControlledNeuron

interface Environment {
	val externalSignals: Collection<ExternallyControlledNeuron>
	fun tick()
	val timeStep: Long
	var onSignalUpdate: ((ExternallyControlledNeuron, Boolean) -> Unit)?
}