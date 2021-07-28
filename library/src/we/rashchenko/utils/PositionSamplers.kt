package we.rashchenko.utils

import java.util.*
import kotlin.math.max



interface PositionSampler{
	fun next(): Vector2
}

class RectangularRandomSampler(private val aspectRatio: Vector2): PositionSampler{
	private val random = Random()
	private val normalisedAspectRation = max(aspectRatio.x, aspectRatio.y).let {
			maxDim -> Vector2(aspectRatio.x / maxDim, aspectRatio.y / maxDim)}
	override fun next(): Vector2 {
		var x: Float
		var y: Float
		while (true) {
			x = random.nextFloat()
			y = random.nextFloat()
			if (x < normalisedAspectRation.x && y < normalisedAspectRation.y) {
				break
			}
		}

		return Vector2(x / normalisedAspectRation.x, y / normalisedAspectRation.y)
	}
}