package we.rashchenko.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RandomIdsTest {

	@Test
	fun testForSetProperties() {
		val dataSize = 1000000
		val data = mutableSetOf<Int>()
		repeat(dataSize){
			data.add(randomIds.next())
		}
		assertEquals(data.size, dataSize)
	}
}