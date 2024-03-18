package org.team9432.robot.led

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.util.Color
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin

object LEDModes {
    fun solid(color: Color, indices: List<Int>) {
        indices.forEach { LEDs.buffer.setLED(it, color) }
    }

    fun strobe(color: Color, duration: Double, indices: List<Int>) {
        val on = Timer.getFPGATimestamp() % duration / duration > 0.5
        solid(if (on) color else Color.kBlack, indices)
    }

    fun pulse(bg: Color, fg: Color, indices: List<List<Int>>, duration: Double = 1.0, cooldown: Double = 1.0) {
        val timestamp = Timer.getFPGATimestamp() % (duration + cooldown)
        // Set everything to the background color
        for (strip in indices) solid(bg, strip)
        // Stop if it is in cooldown
        if (timestamp > duration) return

        // Set one to be lit up in each strip
        for (strip in indices) {
            val stepTime = duration / strip.size
            val position = (timestamp / stepTime).toInt()

            LEDs.buffer.setLED(strip[position], fg)
        }
    }

    val sidePulses = listOf(
        LEDs.Section.SPEAKER_LEFT,
        LEDs.Section.SPEAKER_RIGHT.reversed(),
        LEDs.Section.AMP_LEFT,
        LEDs.Section.AMP_RIGHT.reversed()
    )

    fun breath(c1: Color, c2: Color, indices: List<Int>, duration: Double = 1.0, timestamp: Double = Timer.getFPGATimestamp()) {
        val x = timestamp % duration / duration * 2.0 * Math.PI
        val ratio = (sin(x) + 1.0) / 2.0
        val red = c1.red * (1 - ratio) + c2.red * ratio
        val green = c1.green * (1 - ratio) + c2.green * ratio
        val blue = c1.blue * (1 - ratio) + c2.blue * ratio
        solid(Color(red, green, blue), indices)
    }

    fun rainbow(cycleLength: Double, duration: Double, indices: List<Int>) {
        var x = (1 - Timer.getFPGATimestamp() / duration % 1.0) * 180.0
        val xDiffPerLed = 180.0 / cycleLength
        for (index in indices) {
            x += xDiffPerLed
            x %= 180.0
            LEDs.buffer.setHSV(index, x.toInt(), 255, 255)
        }
    }

    fun wave(c1: Color, c2: Color, cycleLength: Double, duration: Double, indices: List<Int>) {
        val WAVE_EXPONENT = 0.4

        var x = (1 - Timer.getFPGATimestamp() % duration / duration) * 2.0 * Math.PI
        val xDiffPerLed = 2.0 * Math.PI / cycleLength
        for (index in indices) {
            x += xDiffPerLed
            var ratio = (sin(x).pow(WAVE_EXPONENT) + 1.0) / 2.0
            if (java.lang.Double.isNaN(ratio)) {
                ratio = (-sin(x + Math.PI).pow(WAVE_EXPONENT) + 1.0) / 2.0
            }
            if (java.lang.Double.isNaN(ratio)) {
                ratio = 0.5
            }
            val red = c1.red * (1 - ratio) + c2.red * ratio
            val green = c1.green * (1 - ratio) + c2.green * ratio
            val blue = c1.blue * (1 - ratio) + c2.blue * ratio
            LEDs.buffer.setLED(index, Color(red, green, blue))
        }
    }

    fun stripes(colors: List<Color>, length: Int, duration: Double, indices: List<Int>) {
        for (index in indices) {
            var colorIndex = (floor((index - (Timer.getFPGATimestamp() % duration / duration * length * colors.size)) / length) + colors.size).toInt() % colors.size
            colorIndex = colors.size - 1 - colorIndex
            LEDs.buffer.setLED(index, colors[colorIndex])
        }
    }
}