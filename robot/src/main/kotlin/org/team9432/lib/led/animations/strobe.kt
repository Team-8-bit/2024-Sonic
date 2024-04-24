package org.team9432.lib.led.animations

import org.team9432.lib.coroutines.delay
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.predefined.Black
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.Section
import org.team9432.lib.unit.Time

/**
 * Flashes a color on and off at the given period.
 *
 * Does not end.
 *
 * @param color the color to set.
 * @param period the time of one on/off cycle.
 */
fun Section.strobe(
    color: Color,
    period: Time,
) = object: Animation(this) {
    override suspend fun runAnimation() {
        colorset.resetToDefault()

        var isOn = false
        while (true) {
            delay(period / 2)
            isOn = !isOn

            colorset.setProlongedColor(if (isOn) color else Color.Black)
        }
    }
}