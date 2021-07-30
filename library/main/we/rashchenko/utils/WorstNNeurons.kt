package we.rashchenko.utils

import we.rashchenko.neurons.Neuron
import java.util.*
import kotlin.Comparator

/**
Always sorted set (like TreeSet) but with limited capacity. If there is no
 vacant space left, and you are trying to add more it removes the least element.

Note that this is set, so it can not store the same element. And the same element is defined be
 comparator(o1,o2)==0. So be careful using BestN for complex objects, but defining comparator based on
 just one field of it.

For example
 `val data = BestN<Pair<String, Int>>(5){ o1,o2 -> o1.second.compareTo(o2.second) }`
 `data.apply{ add("Apple" to 1); add("Orange" to 1)}`
 `data.size == 1 // one of the elements was not added because comparator think that they are identical`
 */
open class BestN<T>(private val n: Int, comparator: Comparator<T>): TreeSet<T>(comparator) {
	override fun add(element: T): Boolean {
		if (contains(element)){
			return false
		}
		return if (size + 1 > n){
			if (comparator().compare(this.first(), element) < 0){
				pollFirst()
				super.add(element)
			} else{
				false
			}
		} else{
			super.add(element)
		}
	}
}

// @todo probably BestN based on set is not exactly what we want for ChNN purposes. To use it we need to define
//   quite complex Comparator that does not let removing different elements with the same score. Probably the best
//   data structure here is SortedList (ideally based on BinaryTree inside), but it is not implemented as stdlib.

class NeuronsWithFeedbackComparator: Comparator<Pair<Neuron, Feedback>>{
	override fun compare(o1: Pair<Neuron, Feedback>?, o2: Pair<Neuron, Feedback>?): Int {
		return if (o1 == null && o2 == null){
			0
		} else if (o1 == null){
			-1
		} else if (o2 == null){
			1
		} else{
			o1.second.value.compareTo(o2.second.value).let{
				if (it == 0){
					// we need that case to no delete different elements with the same feedback
					o1.first.hashCode().compareTo(o2.first.hashCode())
				}
				else{
					it
				}
			}
		}
	}
}

class InvertedNeuronsWithFeedbackComparator: Comparator<Pair<Neuron, Feedback>>{
	private val baseComparator = NeuronsWithFeedbackComparator()
	override fun compare(o1: Pair<Neuron, Feedback>?, o2: Pair<Neuron, Feedback>?): Int {
		return -baseComparator.compare(o1, o2)
	}
}

class WorstNNeurons(n: Int): BestN<Pair<Neuron, Feedback>>(n, InvertedNeuronsWithFeedbackComparator())
