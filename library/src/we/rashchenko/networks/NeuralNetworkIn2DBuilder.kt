package we.rashchenko.networks

import we.rashchenko.environments.Environment
import we.rashchenko.neurons.Neuron
import we.rashchenko.utils.Vector2


abstract class NeuralNetworkIn2DBuilder(private val nn: NeuralNetworkIn2DSpace) {
	abstract fun neuronsWithPositionSampler(): Pair<Neuron, Vector2>
	abstract fun environmentInputsPositionSampler(environment: Environment): Collection<Vector2>
	abstract fun connectionsSampler(allPositions: Collection<Vector2>): Map<Vector2, Collection<Vector2>>

	private val coordinateNeurons = mutableMapOf<Vector2, Neuron>()

	fun addNeurons(numberOfNeurons: Int){
		repeat(numberOfNeurons){
			val (neuron, coordinate) = neuronsWithPositionSampler()
			nn.add(neuron, coordinate)
			coordinateNeurons[coordinate] = neuron
		}
	}

	fun addEnvironment(environment: Environment){
		environment.externalSignals.zip(environmentInputsPositionSampler(environment)).forEach { (neuron, coordinate)->
			nn.addExternal(neuron, coordinate)
			coordinateNeurons[coordinate] = neuron
		}
	}

	fun addConnections(){
		val connections = connectionsSampler(coordinateNeurons.keys)
		coordinateNeurons.forEach{ (sourceCoordinate, sourceNeuron) ->
			connections[sourceCoordinate]?.forEach{ receiverCoordinate ->
				val receiverNeuron = coordinateNeurons[receiverCoordinate]!!
				nn.addConnection(sourceNeuron, receiverNeuron)
			}
		}
	}
}