package org.team9432.robot.led.animation.groups

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.team9432.lib.delay
import org.team9432.lib.unit.Time
import org.team9432.robot.led.animation.AnimationManager
import org.team9432.robot.led.animation.AnimationJob

class WaitAnimation(val time: Time): AnimationJob {
    override var job: Job? = null

    override fun start() {
        job = AnimationManager.animationScope.launch {
            delay(time)
        }
    }
}