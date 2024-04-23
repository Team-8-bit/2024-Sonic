package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.seconds

fun Section.pulse(
    color: Color,
    duration: Time = 1.seconds,
    cooldown: Time = 1.seconds,
    runReversed: Boolean = false,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colors.resetToDefault()

        // Time each led will be active for
        val timePerStep = duration / colors.ledCount

        val stepsInOrder = colors.indices.toList().let { if (runReversed) it.reversed() else it }

        while (true) {
            for (step in stepsInOrder) {
                colors.setCurrentlyFadingColor(step, color)
                delay(timePerStep)
            }
            delay(cooldown)
        }
    }
}