package we.rashchenko.utils

import java.util.*

class BestN<T>(private val n: Int, comparator: Comparator<T>): TreeSet<T>(comparator) {
	override fun add(element: T): Boolean {
		return if (size > n){
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