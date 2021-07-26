package we.rashchenko.networks

import we.rashchenko.networks.builders.NeuralNetworkEvolutionBuilder

class EvolvingNeuralNetwork(
	private val neuralNetwork: NeuralNetwork, private val neuralNetworkEvolutionBuilder: NeuralNetworkEvolutionBuilder,
	private val evolveEveryNTicks: Long = 10000, private val neuronsForSelection: Int = 1
) : NeuralNetwork by neuralNetwork {
	override fun tick() {
		neuralNetwork.tick()
		if (timeStep % evolveEveryNTicks == 0L){
			neuralNetworkEvolutionBuilder.evolutionStep(neuronsForSelection)
		}
	}
}
