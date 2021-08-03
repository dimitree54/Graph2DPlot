package we.rashchenko.networks

import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.Vector2

class NeuralNetworkIn2DSpace(private val nn: NeuralNetwork) : NeuralNetwork by nn {
	private val neuronsOnCoordinate = mutableMapOf<Vector2, Neuron>()
	private val positions = mutableMapOf<Neuron, Vector2>()
	fun add(neuron: Neuron, position: Vector2) {
		nn.add(neuron)
		positions[neuron] = position
	}

	fun addExternal(neuron: Neuron, position: Vector2) {
		nn.addExternal(neuron)
		positions[neuron] = position
	}

	fun getPosition(neuron: Neuron): Vector2? {
		return positions[neuron]
	}

	fun getAllPositions(): Collection<Vector2> = positions.values
	fun getNeuron(position: Vector2): Neuron? {
		return neuronsOnCoordinate[position]
	}

	override fun remove(neuron: Neuron) {
		getPosition(neuron)?.let { neuronsOnCoordinate.remove(it) }
		positions.remove(neuron)
	}
}

fun NeuralNetworkIn2DSpace.getActiveAndPassiveCoordinates(scale: Vector2 = Vector2.ONES):
		Pair<List<Vector2>, List<Vector2>> {
	val (activeNeurons, passiveNeurons) = this.neurons.partition { it.active }
	return activeNeurons.map { getPosition(it)!!.scl(scale) } to
			passiveNeurons.map { getPosition(it)!!.scl(scale) }
}

fun NeuralNetworkIn2DSpace.getExternalCoordinates(scale: Vector2 = Vector2.ONES): List<Vector2> {
	return externalNeurons.map { getPosition(it)!!.scl(scale) }
}

fun NeuralNetworkIn2DSpace.getConnections(scale: Vector2 = Vector2.ONES): List<Pair<Vector2, Vector2>> {
	return connections.map { (source, receivers) ->
		receivers.map { receiver -> getPosition(source)!!.scl(scale) to getPosition(receiver)!!.scl(scale) }
	}.flatten()
}