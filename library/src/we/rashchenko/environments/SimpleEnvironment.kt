package we.rashchenko.environments

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import we.rashchenko.neurons.StochasticExternalBinaryNeuron
import we.rashchenko.neurons.ExternalBinaryNeuron
import java.util.*

class SimpleEnvironment(private val tickPeriod: Int) : Environment {
	override val externalSignals: Collection<ExternalBinaryNeuron> =
		listOf(StochasticExternalBinaryNeuron(), StochasticExternalBinaryNeuron())

	override var timeStep: Long = 0
		private set

	private val random = Random()
	override fun tick(){
		if (timeStep % tickPeriod == 0L){
			val newValue = random.nextBoolean()
			externalSignals.forEach { it.active = newValue }
		}
		timeStep++
	}

	override var running: Boolean = false
		private set

	@ObsoleteCoroutinesApi
	override suspend fun run(onTick: ()->Unit) {
		coroutineScope{
			launch(context = newSingleThreadContext("EnvironmentThread")) {
				while (running){
					tick()
					onTick()
				}
			}
		}
	}

	override fun pause() {
		running = false
	}
}