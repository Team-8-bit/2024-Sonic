package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.seconds

class Pulse(
    private val color: Color,
    section: Section,
    private val duration: Time = 1.seconds,
    private val cooldown: Time = 1.seconds,
    private val runReversed: Boolean = false,
): Animation(section) {
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