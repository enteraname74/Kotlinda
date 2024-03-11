package com.github.enteraname74.model

/**
 * Represent a tuple, used to be stored in a TupleSpace.
 */
open class Tuple(val elements: List<TupleTemplate>) {

    private val size = elements.size

    operator fun get(pos: Int) = elements.getOrNull(pos)

    /**
     * Check if the tuple is the same as another one.
     */
    fun isSameTuple(other: Tuple): Boolean {
        if (size != other.size) return false

        for (i in 0 until size) {
            if (this[i]?.isSame(other[i]) == false) return false
        }

        return true
    }

    override fun toString(): String {
        return elements.toString()
    }
}

/**
 * Tuple that only holds ReadSimpleTupleTemplates.
 */
class SimpleTuple(elements: List<ReadSimpleTupleTemplate>): Tuple(elements = elements)

/**
 * Utility method for creating a Tuple.
 */
fun T(vararg element : TupleTemplate) = Tuple(element.asList())

/**
 * Utility method for creating a SimpleTuple
 */
fun T(vararg element : ReadSimpleTupleTemplate) = SimpleTuple(element.asList())
