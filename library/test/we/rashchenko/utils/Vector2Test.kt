package we.rashchenko.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Vector2Test {
	private val eps = 0.00001f

	@Test
	fun assertEqual() {
		assertTrue(Vector2.ZERO == Vector2(0f, 0f))
		assertTrue(Vector2.ONES == Vector2(1f, 1f))
		assertTrue(Vector2(2f, 0.5f) == Vector2(2f, 0.5f))
	}

	@Test
	fun dst() {
		assertEquals(Vector2.ZERO.dst(Vector2.ZERO), 0f, eps)
		assertEquals(Vector2.ZERO.dst(Vector2.ONES), Vector2.ONES.dst(Vector2.ZERO), eps)
		assertEquals(Vector2(15f, 1f).dst(Vector2(15f, 2f)), 1f, eps)
	}

	@Test
	fun scl() {
		assertTrue(Vector2.ZERO.scl(Vector2.ONES) == Vector2.ZERO)
		assertTrue(Vector2.ONES.scl(Vector2.ZERO) == Vector2.ZERO)
		assertTrue(Vector2.ZERO.scl(Vector2.ZERO) == Vector2.ZERO)
		assertTrue(Vector2.ONES.scl(Vector2.ONES) == Vector2.ONES)
		assertTrue(Vector2(2f, -0.5f).scl(Vector2(0.5f, -2f)) == Vector2.ONES)
	}
}