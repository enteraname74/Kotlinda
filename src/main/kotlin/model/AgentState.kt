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

    private fun add(variable: Variable) {
        val index = variables.indexOfFirst { it.name == variable.name }

        if (index == -1) variables.add(variable)
        else variables[0] = variable

    }
    fun addAll(vars: List<Variable>) {
        vars.forEach {
            add(it)
        }
    }

    fun getAllVariables() = variables

    override fun toString(): String {
        return "AgentState(variables = $variables)"
    }
}