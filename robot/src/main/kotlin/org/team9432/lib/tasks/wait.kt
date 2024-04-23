package org.team9432.lib.tasks

import org.team9432.lib.delay
import org.team9432.lib.unit.Time

fun wait(time: Time): SuspendRunnable = { delay(time) }