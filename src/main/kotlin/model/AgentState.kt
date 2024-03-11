package com.github.enteraname74.model

/**
 * Holds all variables used by the agent.
 */
class AgentState {
    private val variables: ArrayList<Variable> = ArrayList()

    /**
     * Tries to retrieve a ReadTuple from the variables of the agent.
     *
     * @param variableName the name of the variable in the ReadTuple to retrieve
     *
     * @return the found ReadTuple or null.
     */
    fun get(variableName: String) = variables.find { it.name == variableName }

    fun add(variable: Variable) = variables.add(variable)
    fun addAll(vars: List<Variable>) = variables.addAll(vars)

    fun getAllVariables() = variables
}