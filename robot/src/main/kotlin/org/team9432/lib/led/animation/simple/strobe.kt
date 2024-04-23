package org.team9432.lib.led.animation.simple

import org.team9432.lib.delay
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.predefined.Black
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time

fun Section.strobe(
    color: Color,
    duration: Time,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colors.resetToDefault()

        var isOn = false
        while (true) {
            delay(duration / 2)
            isOn = !isOn

            colors.setProlongedColor(if (isOn) color else Color.Black)
        }
    }
}