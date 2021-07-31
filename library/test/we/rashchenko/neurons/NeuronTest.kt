package we.rashchenko.neurons

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal abstract class NeuronTest {

	abstract fun getInstance(): Neuron

	@Test
	fun testMemoryUsage() {
	}

	@Test
	fun testRunTime() {
	}
}