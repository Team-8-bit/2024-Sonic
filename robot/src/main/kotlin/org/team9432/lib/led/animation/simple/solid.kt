package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.seconds

fun Section.solid(
    color: Color,
    duration: Time = Integer.MAX_VALUE.seconds,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colors.applyToEach {
            resetToDefault()
            prolongedColor = color
        }

        delay(duration)
    }
}