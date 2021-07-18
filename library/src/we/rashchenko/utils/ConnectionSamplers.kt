package we.rashchenko.utils

fun kNearest(k: Int, allPositions: Collection<Vector2>): Map<Vector2, Collection<Vector2>>{
	val result = mutableMapOf<Vector2, Collection<Vector2>>()
	allPositions.forEach { sourcePosition ->
		result[sourcePosition] = allPositions.sortedBy { it.dst(sourcePosition) }.slice(1..k)
	}
	return result
}