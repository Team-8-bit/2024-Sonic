package org.team9432.lib.led.animation.simple

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.milliseconds

class BounceToColor(
    private val color: Color,
    val section: Section,
    private val leadColor: Color = color,
    private val runReversed: Boolean = false,
    private val timePerStep: Time = 20.milliseconds,
): Animation(section) {
    override suspend fun runAnimation(scope: CoroutineScope) {
        colors.setCurrentlyFadingColor(null)

        var maxPosition = colors.indices.last
        var minPosition = colors.indices.first
        var currentPosition: Int
        var currentDirection: Int // 1 or -1

        if (runReversed) {
            currentPosition = maxPosition
            currentDirection = -1
        } else {
            currentPosition = minPosition
            currentDirection = 1
        }

        while (scope.isActive) {
            currentPosition += currentDirection

            if (currentPosition == maxPosition && currentDirection == 1) {
                colors.applyTo(maxPosition) { prolongedColor = color }
                maxPosition--
                currentDirection = -1
            } else if (currentPosition == minPosition && currentDirection == -1) {
                colors.applyTo(minPosition) { prolongedColor = color }
                minPosition++
                currentDirection = 1
            }

            if (minPosition == maxPosition) {
                break
            }

            colors.revert()
            colors.setTemporaryColor(currentPosition, leadColor)

            delay(timePerStep)

            yield()
        }

        colors.applyToEach {
            prolongedColor = color
            temporaryColor = null
        }
    }
}