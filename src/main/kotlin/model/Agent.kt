package com.github.enteraname74.model

import kotlinx.coroutines.*

open class Agent(vararg variable: Variable) {
    private val state: AgentState = AgentState()
    private var shouldRestartItself = false
    private var body: (suspend Agent.() -> Unit) = { }

    constructor(
        legacyAgent: Agent,
        variables: List<Variable>
    ) : this() {
        println("Used constructor for restarting agent")
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
            println("Will rebuild")
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

//    init {
//        println("NEW INSTANCE OF AGENT")
//        this.state.addAll(variable.asList())
//
//        println("Should restart itself ? $shouldRestartItself")
//
//        if (shouldRestartItself) {
//            runBlocking(context = Dispatchers.IO) {
//                delay(1000)
//                def(body)
//                removeJobs(agent = this@Agent)
//                println("Will rebuild")
//                buildNewAgentForRec(legacyAgent = this@Agent, variables = this@Agent.state.getAllVariables())
//            }
//        }
//    }

    suspend infix fun def(body: suspend Agent.() -> Unit) {
        runBlocking {
            this@Agent.body = {body(this)}
        }
        println("END OF BODY OF CURRENT AGENT")
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
        println("START OF STYLED PLUS, BUILD OF JOBS")

        val b1Job = CoroutineScope(Dispatchers.IO).launch {
            this@plus()
        }
        val b2Job = CoroutineScope(Dispatchers.IO).launch {
            other()
        }

        println("WAITING FOR A JOB TO FINISH ${b1Job.isActive} ${b2Job.isActive}")
        while (b1Job.isActive && b2Job.isActive){}
        println("ONE JOB IS FINISHED ! ${b1Job.isCompleted} ${b2Job.isCompleted}")
        if (b1Job.isCompleted) {
            println("B1 COMPLETED")
            b2Job.cancel()
        }
        else if (b2Job.isCompleted) {
            println("B2 COMPLETED")
            b1Job.cancel()
        }

        println("END OF PLUS")
        return this@Agent
    }

    suspend fun simplePlus(b1: suspend () -> Agent, b2: suspend () -> Agent): Agent {
        println("START OF SIMPLE PLUS, BUILD OF JOBS")

        val b1Job = CoroutineScope(Dispatchers.IO).launch {
            b1()
        }
        val b2Job = CoroutineScope(Dispatchers.IO).launch {
            b2()
        }

        println("WAITING FOR A JOB TO FINISH ${b1Job.isActive} ${b2Job.isActive}")
        while (b1Job.isActive && b2Job.isActive){}
        println("ONE JOB IS FINISHED ! ${b1Job.isCompleted} ${b2Job.isCompleted}")
        if (b1Job.isCompleted) {
            println("B1 COMPLETED")
            b2Job.cancel()
        }
        else if (b2Job.isCompleted) {
            println("B2 COMPLETED")
            b1Job.cancel()
        }

        println("END OF PLUS")
        return this
    }

    /**
     * Retrieve a variable from the agent's state.
     */
    fun v(varName: String) = state.get(varName)

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
        println("READ VAR: $tuple")
        state.addAll(ts.read(tuple))
        println("HAS BEEN READ: $tuple")
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
        println("WILL WRITE")
        ts.out(tuple)
        println("HAS WROTE")
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
        println("WILL WRITE")
        ts.out(tuple)
        println("HAS WROTE")
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
        println("WILL POP")
        ts.pop(tuple)
        println("HAS POP")
        return this
    }
}

//fun Agent.j(body: suspend Agent.() -> Unit): LindaJob {
//    val job = LindaJob { body() }
//    println("ADD")
//    jobs.add(job)
//    job.run()
//    return job
//}