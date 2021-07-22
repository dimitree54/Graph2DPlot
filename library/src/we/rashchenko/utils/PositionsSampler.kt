package we.rashchenko.utils

import java.util.*
import kotlin.math.max

val random = Random()
fun rectangularPositionSampler(rectangle: Vector2): Vector2 {
	val maxDim = max(rectangle.x, rectangle.y)
	var x: Float
	var y: Float
	while (true) {
		x = random.nextFloat()
		y = random.nextFloat()
		if (x < rectangle.x / maxDim && y < rectangle.y / maxDim) {
			break
		}
	}

	return Vector2(x * maxDim / rectangle.x, y * maxDim / rectangle.y)
}