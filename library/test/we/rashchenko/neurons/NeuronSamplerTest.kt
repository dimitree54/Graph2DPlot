package we.rashchenko.neurons

import org.junit.jupiter.api.Test
import org.openjdk.jol.info.ClassLayout
import we.rashchenko.neurons.zoo.StochasticNeuronSampler


internal abstract class NeuronSamplerTest {

	abstract fun getInstance(): NeuronsSampler

	@Test
	fun testMemoryUsage() {
		val sampler = getInstance()
		val emptySampler = ClassLayout.parseInstance(sampler).toPrintable()
	}

	@Test
	fun testRunTime() {
	}
}

internal class StochasticNeuronSamplerTest: NeuronSamplerTest() {
	override fun getInstance(): NeuronsSampler {
		return StochasticNeuronSampler()
	}

}