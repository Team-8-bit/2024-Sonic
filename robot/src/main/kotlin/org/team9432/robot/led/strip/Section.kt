package org.team9432.robot.led.strip

class Section(val baseStripPixels: List<Int>) {
    val indices = baseStripPixels.indices
    val ledCount = baseStripPixels.size

    fun getBaseStripIndex(index: Int) = baseStripPixels[index]

    operator fun plus(other: Section) = Section(this.baseStripPixels + other.baseStripPixels)

    fun getColorSet() = StripColorSet(baseStripPixels.map { LEDStrip.currentColors[it] to it })
}