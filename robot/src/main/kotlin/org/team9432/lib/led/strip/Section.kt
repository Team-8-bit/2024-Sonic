package org.team9432.lib.led.strip

import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.PixelColor

class Section(private val baseStripPixels: Set<Int>) {
    operator fun plus(other: Section) = Section(this.baseStripPixels + other.baseStripPixels)
    fun containsBaseStripPixel(pixel: Int) = baseStripPixels.contains(pixel)
    fun getColorSet() = ColorSet(baseStripPixels.associateWith { PixelColor() })

    // Color and base strip index
    class ColorSet internal constructor(val map: Map<Int, PixelColor>) {
        val mapAsList = map.toList()
        val indices = map.keys.indices
        val ledCount = map.size

        operator fun get(index: Int) = map[index] ?: throw Exception("The provided index of $index is not part of this set!")

        /* -------- Full strip methods -------- */

        inline fun forEach(action: (PixelColor) -> Unit) = map.forEach { action.invoke(it.value) }
        inline fun applyToEach(action: PixelColor.() -> Unit) = forEach(action)
        inline fun forEachIndexedBaseStrip(action: (PixelColor, Int) -> Unit) = map.forEach { action.invoke(it.value, it.key) }
        inline fun applyToEachIndexedBaseStrip(action: PixelColor.(Int) -> Unit) = forEachIndexedBaseStrip(action)

        fun resetToDefault() = applyToEach(PixelColor::resetToDefault)
        fun revert() = applyToEach(PixelColor::revertColor)

        fun setCurrentlyFadingColor(color: Color?) = applyToEach { currentlyFadingColor = color }
        fun setProlongedColor(color: Color) = applyToEach { prolongedColor = color }
        fun setTemporaryColor(color: Color?) = applyToEach { temporaryColor = color }
        fun setFadeSpeed(speed: Int) = applyToEach { fadeSpeed = speed }

        /* -------- Single pixel methods -------- */
        inline fun applyTo(index: Int, action: PixelColor.() -> Unit) = mapAsList[index].second.action()

        fun resetToDefault(index: Int) = applyTo(index) { resetToDefault() }
        fun revert(index: Int) = applyTo(index) { revertColor() }

        fun setCurrentlyFadingColor(index: Int, color: Color?) = applyTo(index) { currentlyFadingColor = color }
        fun setProlongedColor(index: Int, color: Color) = applyTo(index) { prolongedColor = color }
        fun setTemporaryColor(index: Int, color: Color?) = applyTo(index) { temporaryColor = color }
        fun setFadeSpeed(index: Int, speed: Int) = applyTo(index) { fadeSpeed = speed }
    }
}