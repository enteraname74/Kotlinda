package com.github.enteraname74.model

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex

/**
 * Represent a space that store tuples.
 */
class TupleSpace {
    private val storage: ArrayList<Tuple> = arrayListOf(
        T(s("niveau-H2O", string), v("valeur-H2O", valeur_H2O, float)),
        T(s("niveau-CH4", string), v("valeur-CH4", valeur_CH4, float)),
        T(s("niveau-CO", string), v("valeur-CO", valeur_CO, float)),
        T(s("detection-H2O-haut", string))
    )
    private val mutex = Mutex(locked = false)

    /**
     * Read a tuple from the TupleSpace.
     * It blocks until the value has been read.
     * Once read, it returns the found variables in the tuple.
     *
     * @param varTuple the tuple to read from the storage.
     */
    suspend fun read(varTuple: Tuple): List<Variable> {
        while (true) {
            mutex.lock()
            storage.forEach { tuple ->
                if (varTuple.isSameTuple(tuple)) {
                    val varTuples = varTuple.elements.filterIsInstance<ReadVariableTupleTemplate>()
                    val foundVariables = buildVarListFromInformation(tuple, varTuples)
                    printStorage()
                    mutex.unlock()
                    return foundVariables
                }
            }
            mutex.unlock()
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
            list.add(
                Variable(
                    name = currentVar.variableName,
                    value = variableTuples[i].value
                )
            )
        }

        return list
    }

    suspend fun pop(tuple: Tuple): Tuple {
        while (true) {
            mutex.lock()
            val foundTuple = storage.find { it.isSameTuple(tuple)  }

            if (foundTuple != null) {
                storage.removeIf{ it.isSameTuple(tuple)  }
                printStorage()
                mutex.unlock()
                return foundTuple
            }
            mutex.unlock()
            delay(10)
        }
    }

    /**
     * Save a tuple to the storage.
     *
     * @param tuple the tuple to save.
     */
    suspend fun out(tuple: Tuple) {
        mutex.lock()
        println("WILL ADD TUPLE: $tuple")
        storage.add(tuple)
        printStorage()
        mutex.unlock()
    }

    /**
     * Update a tuple to the storage.
     *
     * @param tuple the tuple to save.
     */
    suspend fun add(tuple: Tuple) {
        mutex.lock()
        val index = storage.indexOfFirst { it.isSameTuple(tuple) }
        if (index != -1) storage[index] = tuple
        printStorage()
        mutex.unlock()
    }

    fun printStorage() {
        println("-- STORAGE STATE --")
        storage.forEach {
            println(it)
        }
        println("-- -- --")
    }
}