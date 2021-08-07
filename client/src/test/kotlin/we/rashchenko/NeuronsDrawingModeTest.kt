package we.rashchenko

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NeuronsDrawingModeTest {

	@Test
	fun next() {
		assertTrue(NeuronsDrawingMode.ACTIVITY.next() == NeuronsDrawingMode.FEEDBACK)
		assertTrue(NeuronsDrawingMode.FEEDBACK.next() == NeuronsDrawingMode.EXTERNAL_ONLY_FEEDBACK)
		assertTrue(NeuronsDrawingMode.EXTERNAL_ONLY_FEEDBACK.next() == NeuronsDrawingMode.INTERNAL_ONLY_FEEDBACK)
		assertTrue(NeuronsDrawingMode.INTERNAL_ONLY_FEEDBACK.next() == NeuronsDrawingMode.ACTIVITY)
	}
}