package org.team9432.lib.led.animations

import org.team9432.lib.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.milliseconds
import org.team9432.lib.unit.seconds

/**
 * Sends fading "pulses" of color down a section.
 *
 * Does not end.
 *
 * @param color the of color of pulses.
 * @param cooldown time between the end of one pulse and the start of another.
 * @param runReversed if the animation should run in the opposite direction.
 * @param timePerStep the time between each step.
 */
fun Section.pulse(
    color: Color,
    cooldown: Time = 1.seconds,
    runReversed: Boolean = false,
    timePerStep: Time = 20.milliseconds,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colorset.resetToDefault()

        val stepsInOrder = colorset.indices.toList().let { if (runReversed) it.reversed() else it }

        while (true) {
            for (step in stepsInOrder) {
                colorset.setCurrentlyFadingColor(step, color)
                delay(timePerStep)
            }
            delay(cooldown)
        }
    }
}