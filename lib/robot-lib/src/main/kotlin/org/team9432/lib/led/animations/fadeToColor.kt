package org.team9432.lib.led.animations

import org.team9432.lib.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.unit.Time

/**
 * Fades to a color.
 *
 * Ends after [duration].
 *
 * @param color the color to fade to.
 * @param duration the time to fade before ending.
 * @param speed the speed at which the color is changed to. Higher is faster.
 */
fun Section.fadeToColor(
    color: Color,
    duration: Time,
    speed: Int,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colorset.applyToEachIndexedBaseStrip { index ->
            prolongedColor = color
            currentlyFadingColor = LEDStrip.getColor(index)
            this.fadeSpeed = speed
        }

        delay(duration)
    }
}