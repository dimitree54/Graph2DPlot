package we.rashchenko.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class BestNTest {

	@Test
	fun size() {
		val bestN = BestN<Pair<Int, Int>>(5) { o1, o2 -> o1.second.compareTo(o2.second) }
		assertEquals(bestN.size, 0)
		bestN.add(Pair(1, 5))
		assertEquals(bestN.size, 1)
		bestN.add(Pair(2, 5))
		assertEquals(bestN.size, 2)
	}

	@Test
	fun contains() {
	}

	@Test
	fun testAdd() {
	}

	@Test
	fun testAddAll() {
	}
}