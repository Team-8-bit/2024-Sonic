package org.team9432.lib.led.animations

import org.team9432.lib.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.seconds

/**
 * Fades between a list of colors.
 *
 * Does not end.
 *
 * @param colors the list of colors to cycle through.
 * @param colorDuration the time spent changing to each color.
 * @param speed the speed at which colors are changed to. Higher is faster.
 */
fun Section.breath(
    colors: List<Color>,
    colorDuration: Time = 3.seconds,
    speed: Int = 5,
) = object: Animation(this) {
    init {
        assert(colors.isNotEmpty())
    }

    override suspend fun runAnimation() {
        colorset.applyToEach {
            prolongedColor = colors.first()
            currentlyFadingColor = null
            this.fadeSpeed = speed
        }

        var currentColor = 0
        while (true) {
            colorset.applyToEachIndexedBaseStrip { index ->
                prolongedColor = colors[currentColor]
                currentlyFadingColor = LEDStrip.getColor(index)
            }

            currentColor++
            if (currentColor == colors.size) currentColor = 0

            delay(colorDuration)
        }
    }
}
