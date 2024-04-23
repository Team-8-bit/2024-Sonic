package org.team9432.lib.led.animation.groups

import org.team9432.lib.led.animation.AnimationJob

fun repeatAnimation(animation: AnimationJob, runs: Int? = null) = AnimationJob {
    var runCount = 0
    while ((runs?.let { runCount < it } != false)) {
        animation.run()
        runCount++
    }
}
