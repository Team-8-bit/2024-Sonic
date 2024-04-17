package org.team9432.lib.led.animation.simple

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.yield
import org.team9432.lib.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.predefined.Black
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time

class Strobe(
    private val color: Color,
    private val duration: Time,
    override val section: Section,
): org.team9432.lib.led.animation.Animation() {
    override val colors = section.getColorSet()

    override suspend fun runAnimation(scope: CoroutineScope) {
        colors.resetToDefault()

        var isOn = false
        while (true) {
            delay(duration / 2)
            isOn = !isOn

            val color = if (isOn) color else Color.Black
            colors.setProlongedColor(color)

            yield()
        }
    }
}