package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time

fun Section.fadeToColor(
    color: Color,
    duration: Time,
    fadeSpeed: Int,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colors.applyToEachIndexedBaseStrip { index ->
            prolongedColor = color
            currentlyFadingColor = LEDStrip.getColor(index)
            this.fadeSpeed = fadeSpeed
        }

        delay(duration)
    }
}