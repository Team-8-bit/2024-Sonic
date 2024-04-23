package org.team9432.lib.led.animation.groups

import org.team9432.lib.led.animation.AnimationJob

class RepeatAnimation(private val animation: AnimationJob, private val runs: Int? = null): AnimationJob {
    override suspend fun run() {
        var runCount = 0
        while ((runs?.let { runCount < it } != false)) {
            animation.run()
            runCount++
        }
    }
}
