package we.rashchenko.utils

import kotlin.math.max
import kotlin.math.min

fun Double.clip(minValue: Double = 0.0, maxValue: Double = 1.0): Double{
	return max(minValue, min(maxValue, this))
}