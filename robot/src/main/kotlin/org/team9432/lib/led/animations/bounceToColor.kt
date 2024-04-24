package org.team9432.lib.led.animations

import org.team9432.lib.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.milliseconds

/**
 * Bounces a light back and forth until the section is filled in.
 *
 * Ends once the entire section is filled in.
 *
 * @param color the color being filled in.
 * @param leadColor the color of the moving light, defaults to [color].
 * @param runReversed if the animation should run in the opposite direction.
 * @param timePerStep the time between each step of the moving light.
 */
fun Section.bounceToColor(
    color: Color,
    leadColor: Color = color,
    runReversed: Boolean = false,
    timePerStep: Time = 20.milliseconds,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colorset.setCurrentlyFadingColor(null)

        var maxPosition = colorset.indices.last
        var minPosition = colorset.indices.first
        var currentPosition: Int
        var currentDirection: Int // 1 or -1

        if (runReversed) {
            currentPosition = maxPosition
            currentDirection = -1
        } else {
            currentPosition = minPosition
            currentDirection = 1
        }

        while (true) {
            currentPosition += currentDirection

            if (currentPosition == maxPosition && currentDirection == 1) {
                colorset.applyTo(maxPosition) { prolongedColor = color }
                maxPosition--
                currentDirection = -1
            } else if (currentPosition == minPosition && currentDirection == -1) {
                colorset.applyTo(minPosition) { prolongedColor = color }
                minPosition++
                currentDirection = 1
            }

            if (minPosition == maxPosition) {
                break
            }

            colorset.revert()
            colorset.setTemporaryColor(currentPosition, leadColor)

            delay(timePerStep)
        }

        colorset.applyToEach {
            prolongedColor = color
            temporaryColor = null
        }
    }
}