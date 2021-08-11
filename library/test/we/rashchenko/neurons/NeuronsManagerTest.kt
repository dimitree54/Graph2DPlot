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
			neuronsManager.next(0)
		}
	}

	@Test
	fun next() {
		val neuronsManager = NeuronsManager().apply {
			add(StochasticNeuronSampler())
			add(HebbianNeuronSampler())
		}
		repeat(10000) {
			val neuron = neuronsManager.next(it)
			if (neuron is StochasticNeuron) {
				neuronsManager.reportFeedback(it, Feedback.VERY_POSITIVE)
			} else {
				neuronsManager.reportFeedback(it, Feedback.VERY_NEGATIVE)
			}
		}
		var numStochastic = 0
		var numHebbian = 0
		repeat(10000) {
			val neuron = neuronsManager.next(-it - 1)
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
			neuronsManager.reportFeedback(-1, Feedback.VERY_NEGATIVE)
		}
		neuronsManager.next(1).also { neuronsManager.reportFeedback(1, Feedback.VERY_NEGATIVE) }
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
			neuronsManager.reportDeath(-1)
		}
		neuronsManager.next(1).also { neuronsManager.reportDeath(1) }
	}

	@Test
	fun getSummary() {
		val neuronsManager = NeuronsManager().apply {
			add(StochasticNeuronSampler())
			add(HebbianNeuronSampler())
		}
		println(neuronsManager.getSummary())
		repeat(10000) {
			val neuron = neuronsManager.next(it)
			if (neuron is StochasticNeuron) {
				neuronsManager.reportFeedback(it, Feedback.VERY_POSITIVE)
			} else {
				neuronsManager.reportFeedback(it, Feedback.VERY_NEGATIVE)
			}
		}
		println(neuronsManager.getSummary())
	}
}