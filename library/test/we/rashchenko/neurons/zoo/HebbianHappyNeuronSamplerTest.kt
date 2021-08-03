package we.rashchenko.neurons.zoo

import we.rashchenko.neurons.NeuronSamplerTest
import we.rashchenko.neurons.NeuronsSampler

internal class HebbianHappyNeuronSamplerTest : NeuronSamplerTest() {
	override fun getInstance(): NeuronsSampler {
		return HebbianHappyNeuronSampler()
	}
}