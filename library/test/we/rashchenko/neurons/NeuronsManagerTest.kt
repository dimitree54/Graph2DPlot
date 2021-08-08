package we.rashchenko.neurons

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import we.rashchenko.neurons.zoo.*
import we.rashchenko.utils.Feedback

internal class NeuronsManagerTest {

	@Test
	fun add() {
		val neuronsManager = NeuronsManager().apply {
			add(StochasticNeuronSampler())
			add(HebbianNeuronSampler())
			add(HebbianAngryNeuronSampler())
			add(HebbianHappyNeuronSampler())
		}
		assertThrows<IllegalArgumentException> {
			neuronsManager.add(StochasticNeuronSampler())
		}
	}

	@Test
	fun nextIllegal() {
		val neuronsManager = NeuronsManager()
		assertThrows<Exception> {
			neuronsManager.next()
		}
	}

	@Test
	fun next() {
		val neuronsManager = NeuronsManager().apply {
			add(StochasticNeuronSampler())
			add(HebbianNeuronSampler())
		}
		repeat(10000) {
			val neuron = neuronsManager.next()
			if (neuron is StochasticNeuron) {
				neuronsManager.reportFeedback(neuron, Feedback.VERY_POSITIVE)
			} else {
				neuronsManager.reportFeedback(neuron, Feedback.VERY_NEGATIVE)
			}
		}
		var numStochastic = 0
		var numHebbian = 0
		repeat(10000) {
			val neuron = neuronsManager.next()
			if (neuron is StochasticNeuron) {
				numStochastic++
			} else {
				numHebbian++
			}
		}
		assertTrue(numStochastic.toDouble() / (numStochastic + numHebbian) > 0.8)
		assertTrue(numHebbian.toDouble() / (numStochastic + numHebbian) < 0.2)
	}

	@Test
	fun reportFeedback() {
		val neuronsManager = NeuronsManager().apply {
			add(StochasticNeuronSampler())
			add(HebbianNeuronSampler())
			add(HebbianAngryNeuronSampler())
			add(HebbianHappyNeuronSampler())
		}
		assertThrows<IllegalArgumentException> {
			neuronsManager.reportFeedback(StochasticNeuron(), Feedback.VERY_NEGATIVE)
		}
		neuronsManager.next().also { neuronsManager.reportFeedback(it, Feedback.VERY_NEGATIVE) }
	}

	@Test
	fun reportDeath() {
		val neuronsManager = NeuronsManager().apply {
			add(StochasticNeuronSampler())
			add(HebbianNeuronSampler())
			add(HebbianAngryNeuronSampler())
			add(HebbianHappyNeuronSampler())
		}
		assertThrows<IllegalArgumentException> {
			neuronsManager.reportDeath(StochasticNeuron())
		}
		neuronsManager.next().also { neuronsManager.reportDeath(it) }
	}

	@Test
	fun getSummary() {
		val neuronsManager = NeuronsManager().apply {
			add(StochasticNeuronSampler())
			add(HebbianNeuronSampler())
		}
		println(neuronsManager.getSummary())
		repeat(10000) {
			val neuron = neuronsManager.next()
			if (neuron is StochasticNeuron) {
				neuronsManager.reportFeedback(neuron, Feedback.VERY_POSITIVE)
			} else {
				neuronsManager.reportFeedback(neuron, Feedback.VERY_NEGATIVE)
			}
		}
		println(neuronsManager.getSummary())
	}
}