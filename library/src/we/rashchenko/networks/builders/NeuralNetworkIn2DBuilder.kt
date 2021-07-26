package we.rashchenko.networks.builders

import we.rashchenko.environments.Environment
import we.rashchenko.networks.NeuralNetworkIn2DSpace
import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.ConnectionSampler
import we.rashchenko.utils.PositionSampler


abstract class NeuralNetworkIn2DBuilder(
	private val nn2d: NeuralNetworkIn2DSpace, neuronsSampler: NeuronsSampler,
	private val positionSampler: PositionSampler,
	private val externalPositionSampler: PositionSampler,
	private val connectionsSampler: ConnectionSampler
) : NeuralNetworkEvolutionBuilder(nn2d, neuronsSampler) {
	private fun addNeuronsWithoutConnection(numberOfNeurons: Int) {
		repeat(numberOfNeurons) {
			val neuron = neuronsSampler.next()
			val position = positionSampler.next()
			nn2d.add(neuron, position)
		}
	}

	private fun addEnvironmentWithoutConnection(environment: Environment) {
		environment.externalSignals.associateWith { externalPositionSampler.next() }.forEach { (neuron, position) ->
			nn2d.addExternal(neuron, position)
		}
	}

	private fun connectAll() {
		val connections = connectionsSampler.connectAll(nn2d.getAllPositions())
		connections.forEach { (fromPosition, toPositions) ->
			toPositions.forEach { toPosition ->
				nn2d.addConnection(
					nn2d.getNeuron(fromPosition)!!, nn2d.getNeuron(toPosition)!!
				)
			}
		}
	}

	override fun initialise(numberOfNeurons: Int, environment: Environment) {
		addNeuronsWithoutConnection(numberOfNeurons)
		addEnvironmentWithoutConnection(environment)
		connectAll()
	}

	override fun removeNeurons(neuronsToRemove: Collection<Neuron>) {
		neuronsToRemove.forEach {
			nn.remove(it)
			neuronsSampler.reportDeath(it)
		}
	}

	override fun addNeurons(nNewNeurons: Int) {
		repeat(nNewNeurons) {
			val newNeuron = neuronsSampler.next()
			val newPosition = positionSampler.next()
			nn2d.add(newNeuron, newPosition)
			val newConnections = connectionsSampler.connectNew(newPosition, nn2d.getAllPositions())
			newConnections.forEach { (fromPosition, toPositions) ->
				toPositions.forEach { toPosition ->
					nn2d.addConnection(
						nn2d.getNeuron(fromPosition)!!, nn2d.getNeuron(toPosition)!!
					)
				}
			}
		}
	}
}