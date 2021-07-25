package we.rashchenko.utils

fun kNearest(k: Int, allPositions: Collection<Vector2>): Map<Vector2, Collection<Vector2>> {
	val result = mutableMapOf<Vector2, Collection<Vector2>>()
	allPositions.forEach { sourcePosition ->
		result[sourcePosition] = allPositions.sortedBy { it.dst(sourcePosition) }.slice(1..k)
	}
	return result
}

fun newWithKNearestSymmetrical(
	k: Int,
	newPosition: Vector2,
	allPositions: Collection<Vector2>
): Map<Vector2, Collection<Vector2>> {
	val result = mutableMapOf<Vector2, Collection<Vector2>>()
	val fromConnections = allPositions.sortedBy { it.dst(newPosition) }.slice(1..k)
	result[newPosition] = fromConnections
	fromConnections.forEach { result[it] = listOf(newPosition) }
	return result
}

interface ConnectionSampler{
	fun connectNew(newPosition: Vector2, allPositions: Collection<Vector2>): Map<Vector2, Collection<Vector2>>
	fun connectAll(allPositions: Collection<Vector2>): Map<Vector2, Collection<Vector2>>
}
