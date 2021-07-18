package we.rashchenko.environments

import we.rashchenko.neurons.ExternalBinaryNeuron

interface Environment {
	val externalSignals: Collection<ExternalBinaryNeuron>
	fun tick()
}