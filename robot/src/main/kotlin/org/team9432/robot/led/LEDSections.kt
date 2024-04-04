package org.team9432.robot.led

import org.team9432.robot.led.color.PixelColor
import org.team9432.robot.led.ledinterface.LEDStrip

class LEDSection(private val baseStripPixels: List<Int>) {
    val indices = baseStripPixels.indices
    val ledCount = baseStripPixels.size

    operator fun plus(other: LEDSection): LEDSection {
        return LEDSection(this.baseStripPixels + other.baseStripPixels)
    }

    fun getBaseStripIndex(index: Int) = baseStripPixels[index]

    operator fun get(index: Int) = LEDStrip.colorMap[getBaseStripIndex(index)]
}