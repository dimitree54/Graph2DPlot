package we.rashchenko.networks.builders

import we.rashchenko.base.Activity
import we.rashchenko.environments.Environment
import we.rashchenko.networks.NeuralNetwork
import we.rashchenko.neurons.MirroringNeuron
import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.Feedback
import we.rashchenko.utils.KNearestVectorsConnectionSampler
import we.rashchenko.utils.RandomPositionSampler
import we.rashchenko.utils.Vector2


class NeuralNetworkIn2DBuilder(
	override val neuralNetwork: NeuralNetwork,
	override val neuronsSampler: NeuronsSampler
) : NeuralNetworkBuilder {
	private val positionSampler: Iterator<Vector2> = RandomPositionSampler()
	private val vectorsConnectionSampler = KNearestVectorsConnectionSampler(5)
	private val neuronsOnCoordinate = mutableMapOf<Vector2, Neuron>()
	private val positions = mutableMapOf<Neuron, Vector2>()
	private fun addNeuronWithoutConnection(): Neuron =
		neuronsSampler.next().also { neuron ->
			val position = positionSampler.next()
			positions[neuron] = position
			neuronsOnCoordinate[position] = neuron
			neuralNetwork.add(neuron)
		}

	private fun addNeuronsWithoutConnection(n: Int): List<Neuron> = (0 until n).map { addNeuronWithoutConnection() }

	private val neuronsConnectedToActivity = mutableMapOf<Neuron, Activity>()
	private fun addEnvironmentWithoutConnection(environment: Environment) {
		environment.activities.associateWith { positionSampler.next() }
			.forEach { (activity, position) ->
				val neuron = MirroringNeuron(activity, neuronsSampler.next())
				neuralNetwork.add(neuron)
				positions[neuron] = position
				neuronsOnCoordinate[position] = neuron
				neuronsConnectedToActivity[neuron] = activity
			}
	}

	private fun connectAll() {
		val connections = vectorsConnectionSampler.connectAll(positions.values)
		connections.forEach { (fromPosition, toPositions) ->
			toPositions.forEach { toPosition ->
				neuralNetwork.addConnection(
					neuronsOnCoordinate[fromPosition]!!, neuronsOnCoordinate[toPosition]!!
				)
			}
		}
	}

	fun initialise(numberOfNeurons: Int, environment: Environment) {
		addNeuronsWithoutConnection(numberOfNeurons)
		addEnvironmentWithoutConnection(environment)
		connectAll()
	}

	override fun remove(neuronToRemove: Neuron): Boolean {
		return neuralNetwork.remove(neuronToRemove).also { removed ->
			if (removed) {
				val position = positions[neuronToRemove]!!
				neuronsSampler.reportDeath(neuronToRemove)
				neuronsOnCoordinate.remove(positions[neuronToRemove])
				positions.remove(neuronToRemove)
				if (neuronToRemove in neuronsConnectedToActivity) {
					val activity = neuronsConnectedToActivity[neuronToRemove]!!
					val replacementNeuron = MirroringNeuron(activity, neuronsSampler.next())
					neuronsConnectedToActivity.remove(neuronToRemove)
					neuronsConnectedToActivity[replacementNeuron] = activity
					positions[replacementNeuron] = position
					neuronsOnCoordinate[position] = replacementNeuron
					neuralNetwork.add(replacementNeuron)
					connect(position)
					return false
				}
			}
		}
	}

	private fun connect(position: Vector2) {
		val newConnections = vectorsConnectionSampler.connectNew(position, positions.values)
		newConnections.forEach { (fromPosition, toPositions) ->
			toPositions.forEach { toPosition ->
				neuralNetwork.addConnection(
					neuronsOnCoordinate[fromPosition]!!, neuronsOnCoordinate[toPosition]!!
				)
			}
		}
	}

	override fun addNeuron(): Neuron =
		addNeuronWithoutConnection().also {
			val newPosition = positionSampler.next()
			connect(newPosition)
		}

	fun getPosition(neuron: Neuron): Vector2? = positions[neuron]
}

fun NeuralNetworkIn2DBuilder.getActiveAndPassiveCoordinates(scale: Vector2 = Vector2.ONES):
		Pair<List<Vector2>, List<Vector2>> {
	val (activeNeurons, passiveNeurons) = neuralNetwork.neurons.partition { it.active }
	return activeNeurons.map { getPosition(it)!!.scl(scale) } to passiveNeurons.map { getPosition(it)!!.scl(scale) }
}

fun NeuralNetworkIn2DBuilder.getExternalCoordinates(scale: Vector2 = Vector2.ONES): List<Vector2> =
	neuralNetwork.neurons.filterIsInstance<MirroringNeuron>().map { getPosition(it)!!.scl(scale) }

fun NeuralNetworkIn2DBuilder.getConnectionsWithFeedbackWithFeedback(scale: Vector2 = Vector2.ONES):
		List<Triple<Vector2, Vector2, Feedback>> =
	neuralNetwork.connections.map { (source, receivers) ->
		receivers.map { receiver ->
			Triple(
				getPosition(source)!!.scl(scale), getPosition(receiver)!!.scl(scale),
				receiver.getFeedback(neuralNetwork.getNeuronId(source)!!)
			)
		}
	}.flatten()
