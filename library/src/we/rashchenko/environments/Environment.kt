package we.rashchenko.environments

import we.rashchenko.neurons.ExternalBinaryNeuron

interface Environment {
	val externalSignals: Collection<ExternalBinaryNeuron>
	fun tick()
	val timeStep: Long

	val running: Boolean
	suspend fun run(onTick: ()->Unit)
	fun pause()
}