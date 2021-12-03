package we.rashchenko.gui

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NeuronsDrawingModeTest {

	@Test
	fun next() {
		assertTrue(NeuronsDrawingMode.ACTIVITY.next() == NeuronsDrawingMode.FEEDBACK)
		assertTrue(NeuronsDrawingMode.FEEDBACK.next() == NeuronsDrawingMode.EXTERNAL_FEEDBACK)
		assertTrue(NeuronsDrawingMode.EXTERNAL_FEEDBACK.next() == NeuronsDrawingMode.INTERNAL_FEEDBACK)
		assertTrue(NeuronsDrawingMode.INTERNAL_FEEDBACK.next() == NeuronsDrawingMode.ACTIVITY)
	}
}