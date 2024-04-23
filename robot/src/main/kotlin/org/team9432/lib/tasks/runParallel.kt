package org.team9432.lib.tasks

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun runParallel(vararg operations: SuspendRunnable): SuspendRunnable = {
    coroutineScope {
        for (operation in operations) {
            launch { operation.invoke() }
        }
    }
}