package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.predefined.Black
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time

class Strobe(
    private val color: Color,
    private val duration: Time,
    section: Section,
): Animation(section) {
    override suspend fun runAnimation() {
        colors.resetToDefault()

        var isOn = false
        while (true) {
            delay(duration / 2)
            isOn = !isOn

            val color = if (isOn) color else Color.Black
            colors.setProlongedColor(color)
        }
    }
}