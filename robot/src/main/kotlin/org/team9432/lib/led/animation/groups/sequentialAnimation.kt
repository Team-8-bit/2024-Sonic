package org.team9432.lib.led.animation.groups

import org.team9432.lib.led.animation.AnimationJob

fun sequentialAnimation(vararg animations: AnimationJob) = AnimationJob {
    for (animation in animations) {
        animation.run()
    }
}