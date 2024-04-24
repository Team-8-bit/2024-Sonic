package org.team9432.lib.led.management

import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.PixelColor

/** A class that represents a subsection of leds while keeping a reference of where it is on the base strip. */
class Section(private val baseStripPixels: Set<Int>) {
    /** Combines two sections. */
    operator fun plus(other: Section) = Section(this.baseStripPixels + other.baseStripPixels)

    /** Returns if this section manages a specified pixel on the base strip. */
    fun containsBaseStripPixel(pixel: Int) = baseStripPixels.contains(pixel)

    /** Gets a new color set that uses the same base strip pixels as this section. */
    fun getColorSet() = ColorSet(baseStripPixels)

    /**
     * A class representing the colors on a section of led strip. Each animation manages
     * one of these instances to define its ideal colors. Multiple of these can exist for
     * each pixel, and the [AnimationManager] is responsible for choosing which colors are
     * displayed based on animation priority.
     */
    class ColorSet internal constructor(baseStripPixels: Set<Int>) {
        /** A map of base strip pixels to their color as defined by this class. */
        val map: Map<Int, PixelColor> = baseStripPixels.associateWith { PixelColor() }

        /** A list view of the map, used for operations that are relative to the section, not the base strip. */
        val mapAsList = map.toList()

        /** A list of relative indices of this strip from zero to the length of the section. */
        val indices = map.keys.indices

        /** Gets the color of a base strip pixel, or throws an exception if that pixel isn't managed by this colorset. */
        operator fun get(index: Int) = map[index] ?: throw Exception("The provided index of $index is not part of this set!")


        /* -------- Full strip methods -------- */

        /** Applies the given action to each pixel on the strip. */
        inline fun forEach(action: (PixelColor) -> Unit) = map.forEach { action.invoke(it.value) }

        /** Applies the given action to each pixel on the strip. */
        inline fun applyToEach(action: PixelColor.() -> Unit) = forEach(action)

        /** Applies the given action to each pixel on the strip, and passes its base strip index as well. */
        inline fun forEachIndexedBaseStrip(action: (PixelColor, Int) -> Unit) = map.forEach { action.invoke(it.value, it.key) }

        /** Applies the given action to each pixel on the strip, and passes its base strip index as well. */
        inline fun applyToEachIndexedBaseStrip(action: PixelColor.(Int) -> Unit) = forEachIndexedBaseStrip(action)

        /** Resets each color to the default. */
        inline fun resetToDefault() = applyToEach(PixelColor::resetToDefault)

        /** Reverts each color. */
        inline fun revert() = applyToEach(PixelColor::revertColor)

        /** Sets the currently fading color of each color. */
        inline fun setCurrentlyFadingColor(color: Color?) = applyToEach { currentlyFadingColor = color }

        /** Sets the prolonged color of each color. */
        inline fun setProlongedColor(color: Color) = applyToEach { prolongedColor = color }

        /** Sets the temporary color of each color. */
        inline fun setTemporaryColor(color: Color?) = applyToEach { temporaryColor = color }

        /** Sets the fade speed of each color. */
        inline fun setFadeSpeed(speed: Int) = applyToEach { fadeSpeed = speed }


        /* -------- Single pixel methods -------- */

        /** Applies the given action to a pixel on the strip. */
        inline fun applyTo(index: Int, action: PixelColor.() -> Unit) = mapAsList[index].second.action() // Use map as list to get indices relative to this section, not the base strip

        /** Resets a pixel on the strip to the default. */
        inline fun resetToDefault(index: Int) = applyTo(index) { resetToDefault() }

        /** Reverts a pixel on the strip. */
        inline fun revert(index: Int) = applyTo(index) { revertColor() }

        /** Sets the currently fading color of a pixel on the strip. */
        inline fun setCurrentlyFadingColor(index: Int, color: Color?) = applyTo(index) { currentlyFadingColor = color }

        /** Sets the prolonged color of a pixel on the strip. */
        inline fun setProlongedColor(index: Int, color: Color) = applyTo(index) { prolongedColor = color }

        /** Sets the temporary color of a pixel on the strip. */
        inline fun setTemporaryColor(index: Int, color: Color?) = applyTo(index) { temporaryColor = color }

        /** Sets the fade speed of a pixel on the strip. */
        inline fun setFadeSpeed(index: Int, speed: Int) = applyTo(index) { fadeSpeed = speed }
    }
}