package we.rashchenko.utils

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import we.rashchenko.neurons.zoo.HebbianAngryNeuron
import we.rashchenko.neurons.zoo.HebbianHappyNeuron
import we.rashchenko.neurons.zoo.HebbianNeuron

internal class WorstNNeuronsTest {
	@Test
	fun testWorstNNeurons() {
		val worstNNeurons = WorstNNeurons(3)
		worstNNeurons.addAll(
			listOf(
				HebbianAngryNeuron() to Feedback.VERY_NEGATIVE,
				HebbianNeuron() to Feedback.NEUTRAL,
				HebbianHappyNeuron() to Feedback.VERY_POSITIVE
			)
		)
		assertTrue(worstNNeurons.size == 3)
		worstNNeurons.add(HebbianNeuron() to Feedback.NEUTRAL)
		assertTrue(worstNNeurons.size == 3)

		(HebbianNeuron() to Feedback.VERY_POSITIVE).let {
			worstNNeurons.add(it)
			assertFalse(worstNNeurons.contains(it))
		}

		(HebbianNeuron() to Feedback.VERY_NEGATIVE).let {
			worstNNeurons.add(it)
			assertTrue(worstNNeurons.contains(it))
		}
	}
}