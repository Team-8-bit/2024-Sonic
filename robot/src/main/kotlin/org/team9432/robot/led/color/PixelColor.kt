package org.team9432.robot.led.color

import org.team9432.robot.led.color.predefined.Black

/**
 * Actual Color - The color you see on the strip
 *
 * Fade Color - The color the strip is currently at as it fades to the prolonged color
 *
 * Prolonged Color - The color the pixel will fade or revert to
 *
 * Temporary Color - A color that overrides the fade and prolonged colors until the pixel is reverted
 */
data class PixelColor(
    var currentlyFadingColor: Color? = null,
    var prolongedColor: Color = Color.Black,
    var temporaryColor: Color? = null,
    var fadeSpeed: Int = 25,
) {
    /**
     * Resets [temporaryColor] to `-1`, effectively reverting the pixel to
     * whatever state it would be in without the [temporaryColor] override
     */
    fun revertColor() {
        temporaryColor = null
    }

    var actualColor: Color = Color.Black
        private set

    fun resetToDefault() {
        currentlyFadingColor = null
        prolongedColor = Color.Black
        temporaryColor = null
        fadeSpeed = 25
    }

    fun updateActualColor(color: Color) {
        actualColor = color
    }

    companion object {
        val default = PixelColor()
    }
}