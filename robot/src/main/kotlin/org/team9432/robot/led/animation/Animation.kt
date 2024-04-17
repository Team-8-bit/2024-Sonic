package org.team9432.robot.led.animation

import kotlinx.coroutines.CoroutineScope
import org.team9432.robot.led.color.PixelColor
import org.team9432.robot.led.strip.LEDStrip
import org.team9432.robot.led.strip.Section
import org.team9432.robot.led.strip.StripColorSet

abstract class Animation {
    abstract val section: Section
    abstract val colors: StripColorSet
    abstract suspend fun runAnimation(scope: CoroutineScope)

    fun getIdealBaseStripColors(): MutableList<PixelColor?> {
        val list = LEDStrip.emptyColorList.toMutableList()
        colors.getBasePixelList().forEach { (color, baseIndex) ->
            list[baseIndex] = color
        }
        return list
    }
}