package org.team9432.lib.led.strip

import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.PixelColor
import org.team9432.lib.led.color.blendWith
import org.team9432.lib.led.color.predefined.Black

object LEDStrip {
    private val strip: NativeLedStrip
        get(): NativeLedStrip = stripInstance ?: throw Exception("Please call the create() method before attempting to access the instance!")

    val ledCount get() = strip.ledCount

    private val currentPixelColors = mutableMapOf<Int, PixelColor>()
    private val currentColors = mutableMapOf<Int, Color>()

    fun updateColorsFromMap(colors: List<PixelColor>) {

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

    fun getColor(index: Int) = currentColors[index] ?: Color.Black
    fun getPixelColor(index: Int) = currentPixelColors[index] ?: PixelColor()

    fun render() = strip.render()

    private var stripInstance: NativeLedStrip? = null
    fun create(strip: NativeLedStrip) {
        if (stripInstance == null) stripInstance = strip
        else throw Exception("The led strip was already initialized!")
    }
}