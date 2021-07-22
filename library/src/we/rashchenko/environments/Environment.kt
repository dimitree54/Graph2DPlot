package we.rashchenko.environments

import we.rashchenko.neurons.ExternalNeuron

interface Environment {
	val externalSignals: Collection<ExternalNeuron>
	fun tick()
	val timeStep: Long

	val running: Boolean
	suspend fun run(onTick: ()->Unit)
	fun pause()
}