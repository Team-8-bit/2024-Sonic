package org.team9432.lib.led.color

import org.team9432.lib.led.color.predefined.Black

/**
 * Representing the color of a single pixel on a led strip.
 *
 * Currently Fading Color - The color the strip is currently at as it fades to the prolonged color
 *
 * Prolonged Color - The color the pixel will fade or revert to
 *
 * Temporary Color - A color that overrides the fade and prolonged colors until the pixel is reverted
 *
 * Fade Speed - The speed at which the color will fade towards the prolonged color.
 */
data class PixelColor(
    var currentlyFadingColor: Color? = null,
    var prolongedColor: Color = Color.Black,
    var temporaryColor: Color? = null,
    var fadeSpeed: Int = 25,
) {
    /**
     * Resets [temporaryColor] to null, reverting the pixel to whatever state it
     * would be in without the [temporaryColor] override.
     */
    fun revertColor() {
        temporaryColor = null
    }

    /** Resets this color to the defaults. */
    fun resetToDefault() {
        currentlyFadingColor = null
        prolongedColor = Color.Black
        temporaryColor = null
        fadeSpeed = 25
    }
}