package org.team9432.robot.led.color

import kotlin.math.*
import edu.wpi.first.wpilibj.util.Color as WPIColor

/**
 * Class for representing colors in RGB and HSV color modes.
 * Allows for quick conversion between the two modes.
 * All values are in ranges supported by the WPILib `AddressableLED` class.
 * RGB values are in the range [0-255].
 * HSV values are in the range [0-180], [0-255], [0-255].
 */
sealed interface Color {
    fun toWPILibColor(): WPIColor

    companion object
}

fun Color.Companion.fromRGBString(string: String): Color {
    if (string.length != 6) throw Exception("Color string must be 6 characters long")

    return RGBColor(
        string.substring(0, 2).toInt(16),
        string.substring(2, 4).toInt(16),
        string.substring(4, 6).toInt(16)
    )
}

fun Color.getAsRgb(): RGBColor = when (this) {
    is HSVColor -> this.toRGB()
    is RGBColor -> this
}

fun Color.getAsHsv(): HSVColor = when (this) {
    is HSVColor -> this
    is RGBColor -> this.toHSV()
}

fun Color.blendWith(other: Color, amountOfOverlay: Int) = blend(this, other, amountOfOverlay)

/**
 * Blend two colors together and return a new color.
 *
 * Adapted from the FastLED library.
 *
 * @param existing The starting color
 * @param overlay The color to blend toward
 * @param amountOfOverlay The proportion (0-255) of `overlay` to blend into `existing`
 */
fun blend(existing: Color, overlay: Color, amountOfOverlay: Int): Color {
    val existingRgb = existing.getAsRgb()
    val overlayRgb = overlay.getAsRgb()

    val r = blend8(existingRgb.red, overlayRgb.red, amountOfOverlay)
    val g = blend8(existingRgb.green, overlayRgb.green, amountOfOverlay)
    val b = blend8(existingRgb.blue, overlayRgb.blue, amountOfOverlay)

    return RGBColor(r, g, b)
}

/**
 * Blend a variable proportion (0-255) of one byte to another.
 *
 * Adapted from a random stackoverflow post, not the FastLED Library.
 *
 * @param a The starting byte value
 * @param b The byte value to blend toward
 * @param amountOfB The proportion (0-255) of b to blend
 * @return A byte value between `a` and `b`, inclusive
 */
private fun blend8(a: Int, b: Int, amountOfB: Int): Int {
    var partial: Int
    val amountOfA = 255 - amountOfB
    partial = a * amountOfA
    partial += a
    partial += (b * amountOfB)
    partial += b
    return if (b > a) min(ceil(partial / 256.0).toInt(), b)
    else max(floor(partial / 256.0).toInt(), b)
}