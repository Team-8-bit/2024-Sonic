package org.team9432.robot.led.strip

import org.team9432.robot.led.color.PixelColor

class Section(private val baseStripPixels: List<Int>) {
    val indices = baseStripPixels.indices
    val ledCount = baseStripPixels.size

    fun getBaseStripIndex(index: Int) = baseStripPixels[index]

    operator fun plus(other: Section) = Section(this.baseStripPixels + other.baseStripPixels)
    operator fun get(index: Int) = LEDStrip.colorMap[getBaseStripIndex(index)]

    fun forEachColor(update: PixelColor.() -> Unit) = indices.forEach { index -> get(index).update() }
    fun applyToIndex(index: Int, update: PixelColor.() -> Unit) = get(index).update()

    fun revertStrip() = indices.forEach { get(it).revertColor() }
}