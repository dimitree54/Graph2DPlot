package we.rashchenko.neurons

import org.junit.jupiter.api.Test
import org.openjdk.jol.info.GraphLayout
import we.rashchenko.utils.Feedback
import java.util.*
import kotlin.system.measureTimeMillis
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
			repeat(numNeurons){
				neurons.add(sampler.next())
			}
			// imitating work
			repeat(numSamplerTicks){
				sampler.reportFeedback(neurons[r.nextInt(neurons.size)], Feedback(r.nextDouble() * 2 - 1))

				val i = r.nextInt(neurons.size)
				sampler.reportDeath(neurons[i])
				neurons.removeAt(i)
				neurons.add(sampler.next())
			}
			neuronToTest = sampler.next()  // neuron from the middle of sequence
			neurons.forEach{ sampler.reportDeath(it)}
		}.also { assertTrue(it < timeLimitMillisForSampler) }

		val sizeAfter = GraphLayout.parseInstance(sampler).totalSize()
		assertTrue(sizeAfter < memoryLimitBytesForSampler)

		testMemoryUsageAndRuntimeOfTheNeuron(neuronToTest)
	}

	private fun testMemoryUsageAndRuntimeOfTheNeuron(neuron: Neuron) {
		val r = Random()
		// imitating work
		val fakeNeighboursIds = mutableListOf<Int>().also {
				neighbours -> repeat(numNeighboursForNeuron){ neighbours.add(r.nextInt()) } }
		measureTimeMillis {
			var timeStep = 0L
			repeat(numNeuronTicks){
				if(r.nextDouble() < 0.1){
					neuron.touch(fakeNeighboursIds[r.nextInt(fakeNeighboursIds.size)], timeStep)
				}
				if(r.nextDouble() < 0.1){
					neuron.touch(fakeNeighboursIds[r.nextInt(fakeNeighboursIds.size)], timeStep)
				}
				if(r.nextDouble() < 0.1){
					neuron.update(Feedback(r.nextDouble() * 2 - 1), timeStep)
				}
				if(r.nextDouble() < 0.1){
					neuron.getFeedback(fakeNeighboursIds[r.nextInt(fakeNeighboursIds.size)])
				}
				if(r.nextDouble() < 0.1){
					val i = r.nextInt(fakeNeighboursIds.size)
					neuron.forgetSource(fakeNeighboursIds[i])
					fakeNeighboursIds[i] = r.nextInt()
				}
				timeStep += r.nextInt(100)
				neuron.active
			}
		}.also { assertTrue(it < timeLimitMillisForNeuron) }

		val sizeAfter = GraphLayout.parseInstance(neuron).totalSize()
		assertTrue(sizeAfter < memoryLimitBytesForNeuron)
	}
}