package com.github.enteraname74.model

import kotlinx.coroutines.*

open class Agent(vararg variable: Variable) {
    val state: AgentState = AgentState()
    private var shouldRestartItself = false
    private var body: (suspend Agent.() -> Unit) = { }

    constructor(
        legacyAgent: Agent,
        variables: List<Variable>
    ) : this() {
        this.shouldRestartItself = true
        this.state.addAll(variables)
        this.body = legacyAgent.body
        this.forceLaunchAgent()
    }

    /**
     * Force the launch of the agent.
     */
    private fun forceLaunchAgent() {
        runBlocking(context = Dispatchers.IO) {
            delay(1000)
            def(body)
            buildNewAgentForRec(legacyAgent = this@Agent, variables = this@Agent.state.getAllVariables())
        }
    }

    fun buildNewAgentForRec(legacyAgent: Agent, variables: List<Variable>): Agent {
        return Agent(
            legacyAgent = legacyAgent,
            variables =  variables
        )
    }

    @JvmName("AgentRec")
    fun Agent.Agent(vararg variable: Variable): Agent {
        return buildNewAgentForRec(this, variable.asList())
    }

    suspend infix fun def(body: suspend Agent.() -> Unit) {
        runBlocking {
            this@Agent.body = {body(this)}
        }
        body(this)
    }

    infix fun Boolean.`^`(predicate: Boolean): Boolean = this && predicate
    infix fun Boolean.v(predicate: Boolean): Boolean = this && predicate

    operator fun get(predicate: Boolean): Action {
        return Action(predicate, this)
    }

    suspend fun b(block: suspend Agent.() -> Agent) = suspend {
        block()
    }

    operator fun (suspend () -> Agent).plus(other: suspend () -> Agent): Agent {

        val b1Job = CoroutineScope(Dispatchers.IO).launch {
            this@plus()
        }
        val b2Job = CoroutineScope(Dispatchers.IO).launch {
            other()
        }

        while (b1Job.isActive && b2Job.isActive){}
        if (b1Job.isCompleted) {
            b2Job.cancel()
        }
        else if (b2Job.isCompleted) {
            b1Job.cancel()
        }
        return this@Agent
    }

    /**
     * Retrieve a variable from the agent's state.
     */
    fun v(varName: String): Variable? {
        return state.get(varName)
    }

    /**
     * Read a tuple from the TupleSpace and retrieve the values from the template.
     *
     * @param ts the TupleSpace used for operations.
     * @param tuple the ReadTuple with variables to save.
     *
     * @return the instance of the current Agent
     */
    @JvmName("readVariable")
    suspend fun read(ts: TupleSpace, tuple: Tuple): Agent {
        val variables = ts.read(tuple)
        state.addAll(variables)
        delay(1000)
        return this
    }

    /**
     * Write a tuple in the given TupleSpace.
     *
     * @param ts the TupleSpace used for operations.
     * @param tuple the Tuple to write to the TupleSpace.
     *
     * @return the instance of the current Agent
     */
    suspend fun out(ts: TupleSpace, tuple: Tuple): Agent {
        delay(1000)
        ts.out(tuple)
        return this
    }

    /**
     * Write a tuple in the given TupleSpace.
     *
     * @param ts the TupleSpace used for operations.
     * @param tuple the Tuple to write to the TupleSpace.
     *
     * @return the instance of the current Agent
     */
    suspend fun add(ts: TupleSpace, tuple: SimpleTuple): Agent {
        delay(1000)
        ts.out(tuple)
        return this
    }

    /**
     * Write a tuple in the given TupleSpace.
     *
     * @param ts the TupleSpace used for operations.
     * @param tuple the Tuple to write to the TupleSpace.
     *
     * @return the instance of the current Agent
     */
    suspend fun add(ts: TupleSpace, tuple: Tuple): Agent {
        delay(1000)
        ts.add(tuple)
        return this
    }

    /**
     * Read and pop the tuple if found in the tuple space.
     * Represent the in function but is called pop as the in keyword is already taken
     *
     * @param ts the TupleSpace used for operations.
     * @param tuple the Tuple to write to the TupleSpace.
     *
     * @return the instance of the current Agent
     */
    suspend fun pop(ts: TupleSpace, tuple: Tuple): Agent {
        delay(1000)
        ts.pop(tuple)
        return this
    }
}