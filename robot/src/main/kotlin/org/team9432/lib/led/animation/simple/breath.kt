package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.getAsRgb
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time

fun Section.breath(
    breathColors: List<Color>,
    colorDuration: Time,
    fadeSpeed: Int = 10,
) = object: Animation(this) {
    init {
        assert(breathColors.isNotEmpty())
    }

    private val animationColors = breathColors.map { it.getAsRgb() }

    override suspend fun runAnimation() {
        colors.applyToEach {
            prolongedColor = animationColors.first()
            currentlyFadingColor = null
            this.fadeSpeed = fadeSpeed
        }

        while (true) {
            var currentColor = 0

            while (currentColor < this.animationColors.size) {
                colors.applyToEachIndexedBaseStrip { index ->
                    prolongedColor = animationColors[currentColor]
                    currentlyFadingColor = LEDStrip.getColor(index)
                }
                currentColor++
                delay(colorDuration)
            }
        }
    }
}
