package org.team9432.lib.led.strip

import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.PixelColor
import org.team9432.lib.led.color.blendWith
import org.team9432.lib.led.color.predefined.Black

class LEDStrip private constructor(val strip: NativeLedStrip) {
    var currentPixelColors = List(strip.ledCount) { PixelColor() }
        private set
    var currentColors = MutableList(strip.ledCount) { Color.Black }
        private set

    fun updateColorsFromMap(colors: List<PixelColor>) {
        currentPixelColors = colors

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
            currentColors[index] = nextColor

            if (currentlyFadingColor != null) {
                color.currentlyFadingColor = currentlyFadingColor.blendWith(prolongedColor, color.fadeSpeed)
                if (currentlyFadingColor == prolongedColor) color.currentlyFadingColor = null
            }
        }
    }

    fun render() = strip.render()

    companion object {
        private var instance: LEDStrip? = null
        fun getInstance() = instance ?: throw Exception("Please call the create() method before attempting to access the instance!")
        fun create(strip: NativeLedStrip) {
            if (instance == null) instance = LEDStrip(strip)
            else throw Exception("The led strip was already initialized!")
        }
    }
}