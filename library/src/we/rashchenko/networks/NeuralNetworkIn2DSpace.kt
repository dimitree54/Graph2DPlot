package we.rashchenko.networks

import we.rashchenko.neurons.BinaryNeuron
import we.rashchenko.utils.Vector2

class NeuralNetworkIn2DSpace(private val nn: BinaryNeuralNetwork): BinaryNeuralNetwork by nn {
	private val positions = mutableMapOf<BinaryNeuron, Vector2>()
	fun add(neuron: BinaryNeuron, position: Vector2){
		nn.add(neuron)
		positions[neuron] = position
	}
	fun getPosition(neuron: BinaryNeuron): Vector2?{
		return positions[neuron]
	}
}

fun NeuralNetworkIn2DSpace.getActiveAndPassiveCoordinates(scale: Vector2 = Vector2.ONES):
		Pair<List<Vector2>, List<Vector2>>{
	val (activeNeurons, passiveNeurons) = this.neurons.partition { it.active }
	return activeNeurons.map { this.getPosition(it)!!.scl(scale) } to
			passiveNeurons.map { this.getPosition(it)!!.scl(scale) }
}