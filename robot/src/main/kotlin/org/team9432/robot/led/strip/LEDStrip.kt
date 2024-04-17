package org.team9432.robot.led.strip

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import org.team9432.robot.Devices
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.PixelColor
import org.team9432.robot.led.color.blendWith

object LEDStrip {
    private const val LENGTH = 118

    val emptyColorList = List<PixelColor?>(LENGTH) { null }

    private val controller = AddressableLED(Devices.LED_PORT)
    private val buffer = AddressableLEDBuffer(LENGTH)

    init {
        controller.setLength(LENGTH)
        controller.start()
    }

    var currentColors = List(LENGTH) { PixelColor() }
        private set

    fun updateColorsFromMap(colors: List<PixelColor?>) {
        val nonNullColors = colors.map { it ?: PixelColor.default }
        currentColors = nonNullColors

        for ((index, nullableColor) in nonNullColors.withIndex()) {
            val color = nullableColor

            // Freeze immutable copies of each color state
            val temporaryColor = color.temporaryColor
            val currentlyFadingColor = color.currentlyFadingColor
            val prolongedColor = color.prolongedColor

            val nextColor = when {
                temporaryColor != null -> temporaryColor
                currentlyFadingColor != null -> currentlyFadingColor
                else -> prolongedColor
            }

            setLED(index, nextColor)
            color.updateActualColor(nextColor)

            if (currentlyFadingColor != null) {
                color.currentlyFadingColor = currentlyFadingColor.blendWith(prolongedColor, color.fadeSpeed)
                if (currentlyFadingColor == prolongedColor) color.currentlyFadingColor = null
            }
        }
    }

    private fun setLED(index: Int, color: Color) {
        buffer.setLED(index, color.toWPILibColor())
    }

    fun render() {
        controller.setData(buffer)
    }
}