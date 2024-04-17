package org.team9432.robot.led.animation.groups

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.team9432.robot.led.animation.AnimationManager
import org.team9432.robot.led.animation.AnimationJob

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