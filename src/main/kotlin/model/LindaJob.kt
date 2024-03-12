package com.github.enteraname74.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class LindaJob(
    val block: suspend () -> Agent
) {
    var job: Job? = null

    val isActive: Boolean
        get() = job?.isActive == true

    val isCompleted: Boolean
        get() = job?.isCompleted == true

    fun cancel() {
        job?.cancel()
    }

    fun run() {
        job = CoroutineScope(Dispatchers.IO).launch { block() }
    }
}
