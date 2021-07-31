package we.rashchenko.neurons.zoo

import we.rashchenko.neurons.NeuronSamplerTest
import we.rashchenko.neurons.NeuronsSampler

internal class HebbianAngryNeuronTest: NeuronSamplerTest(){
	override fun getInstance(): NeuronsSampler {
		return HebbianAngryNeuronSampler()
	}
}