package we.rashchenko.networks

import we.rashchenko.feedbacks.Feedback
import we.rashchenko.neurons.Neuron
import we.rashchenko.neurons.NeuronsSampler
import we.rashchenko.utils.ConnectionSampler
import we.rashchenko.utils.PositionSampler
import java.util.*

class EvolvingNeuralNetwork(
	private val neuralNetwork: NeuralNetworkIn2DSpace, private val neuronsSampler: NeuronsSampler,
	private val newConnectionsSampler: ConnectionSampler, private val positionSampler: PositionSampler,
	private val evolveEveryNTicks: Long = 10000, private val neuronsForSelection: Int = 1
) : NeuralNetwork by neuralNetwork {
	override fun tick() {
		neuralNetwork.tick()
		if (timeStep % evolveEveryNTicks == 0L){
			evolve()
		}
	}

	private fun evolve() {
		val losers = TreeSet<Pair<Neuron, Feedback>> { o1, o2 ->
			o1.second.compareTo(o2.second)
		}
		neuralNetwork.neurons.forEach {
			val neuronFeedback = getFeedback(it)
			neuronsSampler.reportFeedback(it, neuronFeedback)
			if (losers.size > neuronsForSelection){
				if (losers.last().second > neuronFeedback){
					losers.pollLast()
					losers.add(Pair(it, neuronFeedback))
				}
			}
			else{
				losers.add(Pair(it, neuronFeedback))
			}
		}
		losers.forEach{
			remove(it.first)
			neuronsSampler.reportDeath(it.first)
		}
		repeat(neuronsForSelection){
			val newNeuron = neuronsSampler.next()
			val newPosition = positionSampler.getPosition()
			neuralNetwork.add(newNeuron, newPosition)
			val newConnections = newConnectionsSampler.connectNew(newPosition, neuralNetwork.getAllPositions())
			newConnections.forEach{ (fromPosition, toPositions) ->
				toPositions.forEach { toPosition ->
					neuralNetwork.addConnection(
						neuralNetwork.getNeuron(fromPosition)!!, neuralNetwork.getNeuron(toPosition)!!)
				}
			}
		}
	}
}
