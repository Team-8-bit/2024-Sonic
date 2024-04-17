package org.team9432.lib.led.animation.groups

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.team9432.lib.led.animation.AnimationJob
import org.team9432.lib.led.animation.AnimationManager

class SequentialAnimation(private vararg val animations: AnimationJob): AnimationJob {
    override var job: Job? = null

    override fun start() {
        val scope = AnimationManager.animationScope

        job = scope.launch {
            for (animation in animations) {
                animation.start()
                animation.join()
            }
        }
    }

    override fun cancel() {
        super.cancel()
        animations.forEach { it.cancel() }
    }
}