package org.team9432.lib.led.animation

import kotlinx.coroutines.CoroutineScope
import org.team9432.lib.led.color.PixelColor
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.Section

abstract class Animation {
    abstract val section: Section
    abstract val colors: Section.ColorSet
    abstract suspend fun runAnimation(scope: CoroutineScope)

    fun getIdealBaseStripColors(): MutableList<PixelColor?> {
        val list = LEDStrip.getInstance().emptyColorList.toMutableList()
        colors.getBasePixelList().forEach { (color, baseIndex) ->
            list[baseIndex] = color
        }
        return list
    }
}