package org.team9432.lib.led.color

/**
 * Represents a color in HSV
 *
 * Hue in the range [0-180]
 *
 * Saturation in the range [0-255]
 *
 * Value in the range [0-255]
 */
data class HSVColor(val hue: Int, val saturation: Int, val value: Int): Color {
    override fun toWPILibColor() = edu.wpi.first.wpilibj.util.Color.fromHSV(hue, saturation, value)

    /** Converts this color to RGB */
    fun toRGB(): RGBColor {
        if (saturation == 0) return RGBColor(value, value, value)

        // The below algorithm is copied from Color.fromHSV and moved here for performance reasons.

        // Loosely based on
        // https://en.wikipedia.org/wiki/HSL_and_HSV#HSV_to_RGB
        // The hue range is split into 60 degree regions where in each region there
        // is one rgb component at a low value (m), one at a high value (v) and one
        // that changes (X) from low to high (X+m) or high to low (v-X)

        // Difference between highest and lowest value of any rgb component
        val chroma = (saturation * value) / 255

        // Because hue is 0-180 rather than 0-360 use 30 not 60
        val region = (hue / 30) % 6

        // Remainder converted from 0-30 to 0-255
        val remainder = Math.round((hue % 30) * (255 / 30.0)).toInt()

        // Value of the lowest rgb component
        val m = value - chroma

        // Goes from 0 to chroma as hue increases
        val x = (chroma * remainder) shr 8

        return when (region) {
            0 -> RGBColor(value, x + m, m)
            1 -> RGBColor(value - x, value, m)
            2 -> RGBColor(m, value, x + m)
            3 -> RGBColor(m, value - x, value)
            4 -> RGBColor(x + m, m, value)
            else -> RGBColor(value, m, value - x)
        }
    }
}