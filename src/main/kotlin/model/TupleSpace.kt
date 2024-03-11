package com.github.enteraname74.model

import kotlinx.coroutines.delay
import kotlin.math.min

/**
 * Represent a space that store tuples.
 */
class TupleSpace {
    private val storage: ArrayList<Tuple> = arrayListOf(
        T(v("level", 10, integer))
    )

    /**
     * Read a tuple from the TupleSpace.
     * It blocks until the value has been read.
     * Once read, it returns the found variables in the tuple.
     *
     * @param varTuple the tuple to read from the storage.
     */
    suspend fun read(varTuple: Tuple): List<Variable> {
        while (true) {
            storage.forEach { tuple ->
                if (varTuple.isSameTuple(tuple)) {
                    val varTuples = varTuple.elements.filterIsInstance<ReadVariableTupleTemplate>()
                    return buildVarListFromInformation(tuple, varTuples)
                }
            }
            delay(10)
        }
    }

    /**
     * Build a list of Variables from information.
     *
     * @param tuple the tuple of values to use to build a variable.
     * @param varTuples the list of the variables tuple to use to build a variable.
     *
     * @return a list of Variables.
     */
    private fun buildVarListFromInformation(tuple: Tuple, varTuples: List<ReadVariableTupleTemplate>): List<Variable> {
        val list = ArrayList<Variable>()
        val variableTuples = tuple.elements.filterIsInstance<ValueTupleTemplate>()

        for (i in varTuples.indices) {
            val currentVar = varTuples[i]
            variableTuples.find { it.type == currentVar.type && it.name == currentVar.name }?.let { valueTemplate ->
                list.add(
                    Variable(
                        name = currentVar.variableName,
                        value = valueTemplate.value
                    )
                )
            }
        }

        return list
    }

    suspend fun pop(tuple: Tuple): Tuple {
        while (true) {
            val foundTuple = storage.find { it.isSameTuple(tuple)  }

            if (foundTuple != null) {
                storage.removeIf{ it.isSameTuple(tuple)  }
                return foundTuple
            }
            delay(10)
        }
    }

    /**
     * Save a tuple to the storage.
     *
     * @param tuple the tuple to save.
     */
    fun out(tuple: Tuple) {
        storage.add(tuple)
    }
}