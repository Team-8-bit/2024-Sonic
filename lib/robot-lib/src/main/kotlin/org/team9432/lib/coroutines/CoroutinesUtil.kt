package org.team9432.lib.coroutines

import kotlinx.coroutines.*
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.inMilliseconds
import java.util.concurrent.Executors


val RIODispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

fun CoroutineScope.rioLaunch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = launch(RIODispatcher, start, block)

/** A suspending function. */
typealias SuspendFunction = suspend () -> Unit

/** Runs a suspending function [runs] times, or forever if null. */
fun SuspendFunction.repeat(runs: Int? = null): SuspendFunction = {
    var runCount = 0
    while ((runs?.let { runCount < it } != false)) {
        this.invoke()
        runCount++
    }
}

/** Runs the given [operations] in parallel and ends when they all finish. */
fun runParallel(vararg operations: SuspendFunction): SuspendFunction = {
    coroutineScope {
        for (operation in operations) {
            launch { operation.invoke() }
        }
    }
}

/** Runs the given [operations] sequentially and ends when they all finish. */
fun runSequential(vararg operations: SuspendFunction): SuspendFunction = {
    for (operation in operations) {
        operation.invoke()
    }
}

/* Waits a given time. */
fun wait(time: Time): SuspendFunction = { delay(time) }

/** Delay for a given [Time]. */
suspend fun delay(time: Time) = delay(time.inMilliseconds.toLong())
