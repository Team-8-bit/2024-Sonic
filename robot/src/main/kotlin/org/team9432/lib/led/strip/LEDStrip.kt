package org.team9432.lib.led.strip

import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.PixelColor
import org.team9432.lib.led.color.blendWith
import org.team9432.lib.led.color.predefined.Black
import org.team9432.lib.led.strip.LEDStrip.create

/**
 * Object for interacting with a led strip defined by user code.
 * [create] must be called before other methods, usually in robot initialization.
 */
object LEDStrip {
    /** The non-null strip instance. */
    private val strip: NativeLedStrip
        get(): NativeLedStrip = stripInstance ?: throw Exception("Please call the create() method before attempting to access the instance!")

    /** The number of leds that are on this strip. */
    val ledCount get() = strip.ledCount

    /** A map of indices to their current [PixelColor]. */
    private val currentColors = mutableMapOf<Int, Color>()

    /** A map of indices to their current [Color]. */
    private val currentPixelColors = mutableMapOf<Int, PixelColor>()

    /**
     * Updates the colors based on a list of [PixelColor]s that is [ledCount] long.
     * This method is also responsible for fading towards the prolonged color and displaying the temporary color if necessary.
     */
    fun updateColors(colors: List<PixelColor>) {
        assert(colors.size == ledCount) // Make sure these are the same size

        for ((index, color) in colors.withIndex()) {
            // Freeze immutable copies of each color state
            val temporaryColor = color.temporaryColor
            val currentlyFadingColor = color.currentlyFadingColor
            val prolongedColor = color.prolongedColor

            val nextColor = when {
                temporaryColor != null -> temporaryColor
                currentlyFadingColor != null -> currentlyFadingColor
                else -> prolongedColor
            }

            strip.setLed(index, nextColor)

            if (currentlyFadingColor != null) {
                color.currentlyFadingColor = currentlyFadingColor.blendWith(prolongedColor, color.fadeSpeed)
                if (currentlyFadingColor == prolongedColor) color.currentlyFadingColor = null
            }

            currentColors[index] = nextColor
            currentPixelColors[index] = color
        }
    }

    /** Gets the [PixelColor] at a specified index of the strip. */
    fun getPixelColor(index: Int) = currentPixelColors[index] ?: PixelColor()

    /** Gets the [Color] at a specified index of the strip. */
    fun getColor(index: Int) = currentColors[index] ?: Color.Black

    /** Displays the currently set colors on the strip. */
    fun render() = strip.render()

    private var stripInstance: NativeLedStrip? = null
    fun create(strip: NativeLedStrip) {
        if (stripInstance == null) stripInstance = strip
        else throw Exception("The led strip was already initialized!")
    }
}