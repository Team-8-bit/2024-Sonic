package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time

class FadeToColor(
    private val color: Color,
    private val duration: Time,
    private val fadeSpeed: Int,
    section: Section,
): Animation(section) {
    override suspend fun runAnimation() {
        colors.applyToEachIndexedBaseStrip { index ->
            prolongedColor = color
            currentlyFadingColor = LEDStrip.getInstance().currentColors[index]
            fadeSpeed = this@FadeToColor.fadeSpeed
        }

        delay(duration)
    }
}