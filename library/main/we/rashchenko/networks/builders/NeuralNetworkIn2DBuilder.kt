package we.rashchenko.networks.builders

import we.rashchenko.base.Activity
import we.rashchenko.environments.Environment
import we.rashchenko.networks.NeuralNetworkWithInput
import we.rashchenko.neurons.MirroringNeuron
import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.*


class NeuralNetworkIn2DBuilder(
	override val neuralNetwork: NeuralNetworkWithInput,
	private val neuronsSampler: NeuronsSampler
) : NeuralNetworkBuilder {
	private val positionSampler: Iterator<Vector2> = RandomPositionSampler()
	private val vectorsConnectionSampler = KNearestVectorsConnectionSampler(5)
	private val neuronsOnCoordinate = mutableMapOf<Vector2, Neuron>()
	private val positions = mutableMapOf<Neuron, Vector2>()
	private val ids = mutableMapOf<Neuron, Int>()
	private fun addNeuronWithoutConnection(): Neuron {
		val id = randomIds.next()
		return neuronsSampler.next(id).also { neuron ->
			val position = positionSampler.next()
			positions[neuron] = position
			neuronsOnCoordinate[position] = neuron
			ids[neuron] = id
			neuralNetwork.add(neuron)
		}
	}

	private fun addNeuronsWithoutConnection(n: Int): List<Neuron> = (0 until n).map { addNeuronWithoutConnection() }

	private val neuronsConnectedToActivity = mutableMapOf<Neuron, Activity>()
	private fun addEnvironmentWithoutConnection(environment: Environment) {
		environment.activities.associateWith { positionSampler.next() }
			.forEach { (activity, position) ->
				val id = randomIds.next()
				val neuron = MirroringNeuron(activity, neuronsSampler.next(id))
				neuralNetwork.addInputNeuron(neuron)
				positions[neuron] = position
				neuronsOnCoordinate[position] = neuron
				ids[neuron] = id
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
				neuronsSampler.reportDeath(ids[neuronToRemove]!!)
				neuronsOnCoordinate.remove(positions[neuronToRemove])
				positions.remove(neuronToRemove)
				if (neuronToRemove in neuronsConnectedToActivity) {
					val id = randomIds.next()
					val activity = neuronsConnectedToActivity[neuronToRemove]!!
					val replacementNeuron = MirroringNeuron(activity, neuronsSampler.next(id))
					neuronsConnectedToActivity.remove(neuronToRemove)
					neuronsConnectedToActivity[replacementNeuron] = activity
					positions[replacementNeuron] = position
					neuronsOnCoordinate[position] = replacementNeuron
					neuralNetwork.add(replacementNeuron)
					ids[replacementNeuron] = id
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
			connect(positions[it]!!)
		}

	fun getPosition(neuron: Neuron): Vector2? = positions[neuron]

	override fun reportFeedback(neuron: Neuron, feedback: Feedback) {
		ids[neuron]?.let { id ->
			neuronsSampler.reportFeedback(id, feedback)
		}
	}
}
