package com.github.enteraname74.model

class Action(private val condition: Boolean, private val agent: Agent) {
    // This function takes a lambda and executes it if the condition is true
    suspend operator fun invoke(block: suspend () -> Unit): Agent {
        if (condition) {
            block()
        }

        return agent
    }
}