package org.team9432.lib.led.animation.groups

import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.team9432.lib.led.animation.AnimationJob
import org.team9432.lib.led.animation.AnimationManager

class ParallelAnimation(private vararg val animations: AnimationJob): AnimationJob {
    override var job: Job? = null

    override fun start() {
        val scope = AnimationManager.animationScope

        job = scope.launch {
            animations.mapNotNull {
                it.start()
                it.job
            }.joinAll()
        }
    }

    override fun cancel() {
        super.cancel()
        animations.forEach { it.cancel() }
    }
}
