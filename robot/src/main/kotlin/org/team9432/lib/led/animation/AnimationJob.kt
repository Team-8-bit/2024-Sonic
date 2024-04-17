package org.team9432.lib.led.animation

import kotlinx.coroutines.Job

interface AnimationJob {
    var job: Job?

    fun start()

    suspend fun join() {
        job?.join()
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}