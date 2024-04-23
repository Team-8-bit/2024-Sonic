package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.milliseconds

class SlideToColor(
    private val color: Color,
    section: Section,
    private val leadColor: Color = color,
    private val runReversed: Boolean = false,
    private val timePerStep: Time = 20.milliseconds,
): Animation(section) {
    override suspend fun runAnimation() {
        colors.setCurrentlyFadingColor(null)

        var runningIndices = colors.indices.toList().let { if (runReversed) it.reversed() else it }

        while (runningIndices.isNotEmpty()) {
            val currentPosition = runningIndices.first()
            runningIndices = runningIndices.drop(1)

            colors.revert()

            colors.applyTo(currentPosition) {
                prolongedColor = color
                temporaryColor = leadColor
            }

            delay(timePerStep)
        }
    }
}