package we.rashchenko.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ExponentialMovingAverageTest {
	private val eps = 0.00001

	@Test
	fun testEMA() {
		ExponentialMovingAverage(8.0, 0.5).let {
			assertEquals(it.value, 8.0, eps)
			it.update(-8.0)
			assertEquals(it.value, 0.0, eps)
			it.update(-8.0)
			assertEquals(it.value, -4.0, eps)
			it.update(-0.0)
			assertEquals(it.value, -2.0, eps)
		}
	}
}