package org.team9432.lib.tasks

fun runSequential(vararg operations: SuspendRunnable): SuspendRunnable = {
    for (operation in operations) {
        operation.invoke()
    }
}