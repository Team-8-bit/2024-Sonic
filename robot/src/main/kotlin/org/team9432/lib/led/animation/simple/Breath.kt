package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.getAsRgb
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time

class Breath(
    colors: List<Color>,
    private val colorDuration: Time,
    private val fadeSpeed: Int = 10,
    section: Section,
): Animation(section) {
    init {
        assert(colors.isNotEmpty())
    }

    private val animationColors = colors.map { it.getAsRgb() }

    override suspend fun runAnimation() {
        colors.applyToEach {
            prolongedColor = animationColors.first()
            currentlyFadingColor = null
            fadeSpeed = this@Breath.fadeSpeed
        }

        while (true) {
            var currentColor = 0

            while (currentColor < this.animationColors.size) {
                colors.applyToEachIndexedBaseStrip { index ->
                    prolongedColor = animationColors[currentColor]
                    currentlyFadingColor = LEDStrip.getInstance().currentColors[index]
                }
                currentColor++
                delay(colorDuration)
            }
        }
    }
}
