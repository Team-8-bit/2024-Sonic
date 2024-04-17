package org.team9432.lib.led.strip

import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.PixelColor

class Section(private val baseStripPixels: Set<Int>) {
    operator fun plus(other: Section) = Section(this.baseStripPixels + other.baseStripPixels)
    fun getColorSet() = ColorSet(baseStripPixels.map { LEDStrip.currentColors[it] to it })

    // Color and base strip index
    class ColorSet internal constructor(private val map: List<Pair<PixelColor, Int>>) {
        val colors = map.map { it.first }

        val indices = map.indices
        val ledCount = map.size

        fun getBasePixelList() = map

        /* -------- Full strip methods -------- */

        inline fun forEach(action: (PixelColor) -> Unit) = colors.forEach(action)
        inline fun applyToEach(action: PixelColor.() -> Unit) = forEach { it.action() }

        fun resetToDefault() = applyToEach { resetToDefault() }
        fun revert() = applyToEach { revertColor() }

        fun setCurrentlyFadingColor(color: Color?) = applyToEach { currentlyFadingColor = color }
        fun setProlongedColor(color: Color) = applyToEach { prolongedColor = color }
        fun setTemporaryColor(color: Color?) = applyToEach { temporaryColor = color }
        fun setFadeSpeed(speed: Int) = applyToEach { fadeSpeed = speed }

        /* -------- Single pixel methods -------- */
        inline fun applyTo(index: Int, action: PixelColor.() -> Unit) = colors[index].action()

        fun resetToDefault(index: Int) = applyTo(index) { resetToDefault() }
        fun revert(index: Int) = applyTo(index) { revertColor() }

        fun setCurrentlyFadingColor(index: Int, color: Color?) = applyTo(index) { currentlyFadingColor = color }
        fun setProlongedColor(index: Int, color: Color) = applyTo(index) { prolongedColor = color }
        fun setTemporaryColor(index: Int, color: Color?) = applyTo(index) { temporaryColor = color }
        fun setFadeSpeed(index: Int, speed: Int) = applyTo(index) { fadeSpeed = speed }
    }
}