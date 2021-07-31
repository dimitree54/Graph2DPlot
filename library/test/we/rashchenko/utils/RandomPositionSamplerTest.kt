package we.rashchenko.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RandomPositionSamplerTest {
	@Test
	fun testNext() {
		RandomPositionSampler().let{ sampler ->
			repeat(100000){
				sampler.next().let { sample ->
					assertTrue(sample.x in 0f .. 1f)
					assertTrue(sample.y in 0f .. 1f)
				}
			}
		}
	}
}