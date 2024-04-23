package org.team9432.lib.tasks

fun SuspendRunnable.repeat(runs: Int? = null): SuspendRunnable = {
    var runCount = 0
    while ((runs?.let { runCount < it } != false)) {
        this.invoke()
        runCount++
    }
}
