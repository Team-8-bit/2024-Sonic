package org.team9432.lib.led.animation.groups

import org.team9432.lib.led.animation.AnimationJob

class SequentialAnimation(private vararg val animations: AnimationJob): AnimationJob {
    override suspend fun run() {
        for (animation in animations) {
            animation.run()
        }
    }
}