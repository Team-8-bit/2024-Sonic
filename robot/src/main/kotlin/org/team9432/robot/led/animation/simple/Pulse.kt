package org.team9432.robot.led.animation.simple

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import org.team9432.lib.delay
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.seconds
import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.strip.Section

class Pulse(
    private val color: Color,
    private val duration: Time = 1.seconds,
    private val cooldown: Time = 1.seconds,
    override val section: Section,
    private val runReversed: Boolean = false,
): Animation() {
    override val colors = section.getColorSet()

    override suspend fun runAnimation(scope: CoroutineScope) {
        colors.resetToDefault()

        // Time each led will be active for
        val timePerStep = duration / colors.ledCount

        val stepsInOrder = colors.indices.toList().let { if (runReversed) it.reversed() else it }

        while (scope.isActive) {
            for (step in stepsInOrder) {
                colors.setCurrentlyFadingColor(step, color)
                delay(timePerStep)
                yield()
            }
            delay(cooldown)
        }
    }
}