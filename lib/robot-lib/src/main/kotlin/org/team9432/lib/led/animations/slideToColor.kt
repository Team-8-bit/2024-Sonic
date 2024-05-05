package org.team9432.lib.led.animations

import org.team9432.lib.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.milliseconds

/**
 * Slides a new color in from one side.
 *
 * Ends once the section is covered.
 *
 * @param color the color being filled in.
 * @param leadColor the color of the first light on the moving part, defaults to [color].
 * @param runReversed if the animation should run in the opposite direction.
 * @param timePerStep the time between each step of the moving light.
 */
fun Section.slideToColor(
    color: Color,
    leadColor: Color = color,
    runReversed: Boolean = false,
    timePerStep: Time = 20.milliseconds,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colorset.setCurrentlyFadingColor(null)

        var runningIndices = colorset.indices.toList().let { if (runReversed) it.reversed() else it }

        while (runningIndices.isNotEmpty()) {
            val currentPosition = runningIndices.first()
            runningIndices = runningIndices.drop(1)

            colorset.revert()

            colorset.applyTo(currentPosition) {
                prolongedColor = color
                temporaryColor = leadColor
            }

            delay(timePerStep)
        }
    }
}