package org.team9432.lib.led.animation.groups

import org.team9432.lib.delay
import org.team9432.lib.led.animation.AnimationJob
import org.team9432.lib.unit.Time

class WaitAnimation(val time: Time): AnimationJob {
    override suspend fun run() = delay(time)
}