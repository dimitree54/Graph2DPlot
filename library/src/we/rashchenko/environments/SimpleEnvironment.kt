package we.rashchenko.environments

import we.rashchenko.neurons.DeterministicExternalBinaryNeuron
import we.rashchenko.neurons.ExternalBinaryNeuron

class SimpleEnvironment : Environment {
	override val externalSignals: Collection<ExternalBinaryNeuron> =
		listOf(
			DeterministicExternalBinaryNeuron().apply { active=true },
			DeterministicExternalBinaryNeuron().apply { active=true })
	override fun tick(){}
}