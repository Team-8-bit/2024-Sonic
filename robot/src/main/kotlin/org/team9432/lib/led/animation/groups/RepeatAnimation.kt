package org.team9432.lib.led.animation.groups

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.team9432.lib.led.animation.AnimationJob
import org.team9432.lib.led.animation.AnimationManager

class RepeatAnimation(private val animation: AnimationJob, private val runs: Int? = null): AnimationJob {
    override var job: Job? = null

    override fun start() {
        val scope = AnimationManager.animationScope

        job = scope.launch {
            var runCount = 0
            while ((runs?.let { runCount < it } != false)) {
                animation.start()
                animation.join()
                runCount++
            }
        }
    }

    override fun cancel() {
        super.cancel()
        animation.cancel()
    }
}
