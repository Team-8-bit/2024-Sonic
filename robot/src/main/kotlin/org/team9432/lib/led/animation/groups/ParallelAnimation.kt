package org.team9432.lib.led.animation.groups

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.team9432.lib.led.animation.AnimationJob

class ParallelAnimation(private vararg val animations: AnimationJob): AnimationJob {
    override suspend fun run() = coroutineScope {
        for (animation in animations) {
            launch { animation.run() }
        }
    }
}