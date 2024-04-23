package org.team9432.lib.led.animation.groups

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.team9432.lib.led.animation.AnimationJob

fun parallelAnimation(vararg animations: AnimationJob) = AnimationJob {
    coroutineScope {
        for (animation in animations) {
            launch { animation.run() }
        }
    }
}