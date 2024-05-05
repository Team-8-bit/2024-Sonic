package org.team9432.lib.led.color

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import edu.wpi.first.wpilibj.util.Color as WPILibColor

/**
 * Class for representing colors in RGB and HSV color modes.
 * Allows for quick conversion between the two modes.
 * RGB values are in the range [0-255].
 * HSV values are in the range [0-180], [0-255], [0-255].
 */
sealed interface Color {
    fun toWPILibColor(): WPILibColor

    companion object // This is used by extension properties elsewhere
}

/** Gets this color in RGB format. */
fun Color.getAsRgb(): RGBColor = when (this) {
    is HSVColor -> this.toRGB()
    is RGBColor -> this
}

/** Gets this color in HSV format. */
fun Color.getAsHsv(): HSVColor = when (this) {
    is HSVColor -> this
    is RGBColor -> this.toHSV()
}

/**
 * Blend this color with another together and return a new color.
 *
 * Adapted from the FastLED library.
 *
 * @param other The color to blend toward
 * @param amountOfOverlay The proportion (0-255) of `overlay` to blend into `existing`
 */
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

fun Color.Companion.fromRGBString(string: String): Color {
    if (string.length != 6) throw Exception("Color string must be 6 characters long")

    return RGBColor(
        string.substring(0, 2).toInt(16),
        string.substring(2, 4).toInt(16),
        string.substring(4, 6).toInt(16)
    )
}