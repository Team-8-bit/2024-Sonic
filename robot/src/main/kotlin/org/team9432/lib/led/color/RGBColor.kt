package org.team9432.lib.led.color

import kotlin.math.max
import kotlin.math.min

/**
 * Represents a color in RGB with red, green, and blue in the range [0-255].
 */
data class RGBColor(val red: Int, val green: Int, val blue: Int): Color {
    override fun toWPILibColor() = edu.wpi.first.wpilibj.util.Color(red, green, blue)

    /** Converts this color to HSV */
    fun toHSV(): HSVColor {
        val red = red / 255.0
        val green = green / 255.0
        val blue = blue / 255.0

        val cMax = max(red, max(green, blue))
        val cMin = min(red, min(green, blue))

        val delta = cMax - cMin

        // Hue
        val hue = if (delta == 0.0) {
            0
        } else if (cMax == red) {
            Math.round(60 * (((green - blue) / delta) % 6)).toInt()
        } else if (cMax == green) {
            Math.round(60 * (((blue - red) / delta) + 2)).toInt()
        } else {
            Math.round(60 * (((red - green) / delta) + 4)).toInt()
        }

        // Saturation
        val saturation = if ((cMax == 0.0)) 0.0 else delta / cMax

        // Convert final values to correct range
        return HSVColor(
            hue / 2,
            Math.round(saturation * 255).toInt(),
            Math.round(cMax * 255).toInt()
        )
    }
}