package we.rashchenko.neurons

import org.junit.jupiter.api.Test
import org.openjdk.jol.info.GraphLayout
import we.rashchenko.utils.Feedback
import we.rashchenko.utils.randomIds
import java.util.*
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal abstract class NeuronSamplerTest {
	private val timeLimitMillisForSampler = 10000
	private val memoryLimitBytesForSampler = 100 * 1024 * 1024  // 10 Mb max for empty sampler after work
	private val numNeurons = 100000
	private val numSamplerTicks = 1000000

	private val timeLimitMillisForNeuron = 1000
	private val memoryLimitBytesForNeuron = 1024 * 1024  // 1 Mb max per Neuron
	private val numNeuronTicks = 1000000
	private val numNeighboursForNeuron = 100

	abstract fun getInstance(): NeuronsSampler

	@Test
	fun testMemoryUsageAndRuntimeOfSampler() {
		val r = Random()
		val sampler = getInstance()
		val neurons = mutableListOf<Neuron>()
		val neuronToTest: Neuron
		measureTimeMillis {
			// imitating init
			repeat(numNeurons) {
				neurons.add(sampler.next())
			}
			// imitating work
			repeat(numSamplerTicks) {
				sampler.reportFeedback(neurons[r.nextInt(neurons.size)], Feedback(r.nextDouble() * 2 - 1))

				val i = r.nextInt(neurons.size)
				sampler.reportDeath(neurons[i])
				neurons.removeAt(i)
				neurons.add(sampler.next())
			}
			neuronToTest = sampler.next()  // neuron from the middle of sequence
			neurons.forEach { sampler.reportDeath(it) }
		}.also { assertTrue(it < timeLimitMillisForSampler) }

		//val sizeAfter = GraphLayout.parseInstance(sampler).totalSize()
		//assertTrue(sizeAfter < memoryLimitBytesForSampler)

		//testMemoryUsageAndRuntimeOfTheNeuron(neuronToTest)
		//testExternalControlling(neuronToTest)
	}

	private fun testMemoryUsageAndRuntimeOfTheNeuron(neuron: Neuron) {
		val r = Random()
		// imitating work
		val fakeNeighboursIds =
			mutableListOf<Int>().also { neighbours -> repeat(numNeighboursForNeuron) { neighbours.add(randomIds.next()) } }
		measureTimeMillis {
			var timeStep = 0L
			repeat(numNeuronTicks) {
				if (r.nextDouble() < 0.1) {
					neuron.touch(fakeNeighboursIds[r.nextInt(fakeNeighboursIds.size)], timeStep)
				}
				if (r.nextDouble() < 0.1) {
					neuron.touch(fakeNeighboursIds[r.nextInt(fakeNeighboursIds.size)], timeStep)
				}
				if (r.nextDouble() < 0.1) {
					neuron.update(Feedback(r.nextDouble() * 2 - 1), timeStep)
				}
				if (r.nextDouble() < 0.1) {
					neuron.getFeedback(fakeNeighboursIds[r.nextInt(fakeNeighboursIds.size)])
				}
				if (r.nextDouble() < 0.1) {
					val i = r.nextInt(fakeNeighboursIds.size)
					neuron.forgetSource(fakeNeighboursIds[i])
					fakeNeighboursIds[i] = r.nextInt()
				}
				timeStep += r.nextInt(100)
				neuron.active
			}
			fakeNeighboursIds.forEach { neuron.forgetSource(it) }
		}.also { assertTrue(it < timeLimitMillisForNeuron) }

		val sizeAfter = GraphLayout.parseInstance(neuron).totalSize()
		assertTrue(sizeAfter < memoryLimitBytesForNeuron)
	}

	private fun testExternalControlling(neuron: Neuron) {
		val r = Random()
		// imitating work
		val fakeNeighboursIds =
			mutableListOf<Int>().also { neighbours -> repeat(numNeighboursForNeuron) { neighbours.add(randomIds.next()) } }

		val externallyControlledNeuron = ExternallyControlledNeuron(neuron)
		var timeStep = 0L
		repeat(numNeuronTicks) {
			if (r.nextDouble() < 0.1) {
				externallyControlledNeuron.externallyControlled = !externallyControlledNeuron.externallyControlled
			}
			if (r.nextDouble() < 0.1) {
				val newValue = r.nextBoolean()
				externallyControlledNeuron.active = newValue
				if (externallyControlledNeuron.externallyControlled) {
					assertEquals(externallyControlledNeuron.active, newValue)
					if (newValue) {
						externallyControlledNeuron.touch(fakeNeighboursIds[0], timeStep)
						assertEquals(
							externallyControlledNeuron.getFeedback(fakeNeighboursIds[0]),
							Feedback.VERY_POSITIVE
						)
					} else {
						externallyControlledNeuron.touch(fakeNeighboursIds[0], timeStep)
						assertEquals(
							externallyControlledNeuron.getFeedback(fakeNeighboursIds[0]),
							Feedback.VERY_NEGATIVE
						)
					}
				} else {
					assertEquals(externallyControlledNeuron.active, neuron.active)
				}
			}
			if (r.nextDouble() < 0.1) {
				externallyControlledNeuron.touch(fakeNeighboursIds[r.nextInt(fakeNeighboursIds.size)], timeStep)
			}
			if (r.nextDouble() < 0.1) {
				externallyControlledNeuron.touch(fakeNeighboursIds[r.nextInt(fakeNeighboursIds.size)], timeStep)
			}
			if (r.nextDouble() < 0.1) {
				externallyControlledNeuron.update(Feedback(r.nextDouble() * 2 - 1), timeStep)
			}
			if (r.nextDouble() < 0.1) {
				externallyControlledNeuron.getFeedback(fakeNeighboursIds[r.nextInt(fakeNeighboursIds.size)])
			}
			if (r.nextDouble() < 0.1) {
				val i = r.nextInt(fakeNeighboursIds.size)
				externallyControlledNeuron.forgetSource(fakeNeighboursIds[i])
				fakeNeighboursIds[i] = r.nextInt()
			}
			timeStep += r.nextInt(100)
		}
		// @todo make test for parallel neuron functions calling
	}
}
