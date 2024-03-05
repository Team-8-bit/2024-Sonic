package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.util.Color
import org.team9432.lib.commandbased.commands.SimpleCommand
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin

object BaseLEDCommands {
    fun solidCommand(color: Color, strip: LEDSubsystem) = SimpleCommand(
        requirements = setOf(strip),
        execute = { solid(color, strip.strip) }
    )

    fun strobeCommand(color: Color, duration: Double, strip: LEDSubsystem) = SimpleCommand(
        requirements = setOf(strip),
        execute = { strobe(color, duration, strip.strip) }
    )

    fun breathCommand(c1: Color, c2: Color, strip: LEDSubsystem, duration: Double = 1.0, timestamp: Double = Timer.getFPGATimestamp()) = SimpleCommand(
        requirements = setOf(strip),
        execute = { breath(c1, c2, strip.strip, duration, timestamp) }
    )

    fun rainbowCommand(cycleLength: Double, duration: Double, strip: LEDSubsystem) = SimpleCommand(
        requirements = setOf(strip),
        execute = { rainbow(cycleLength, duration, strip.strip) }
    )

    fun waveCommand(c1: Color, c2: Color, cycleLength: Double, duration: Double, strip: LEDSubsystem) = SimpleCommand(
        requirements = setOf(strip),
        execute = { wave(c1, c2, cycleLength, duration, strip.strip) }
    )

    fun stripesCommand(colors: List<Color>, length: Int, duration: Double, strip: LEDSubsystem) = SimpleCommand(
        requirements = setOf(strip),
        execute = { stripes(colors, length, duration, strip.strip) }
    )

    fun solid(color: Color, strip: LEDs.Strip) {
        for (index in strip.indices) {
            LEDs.buffer.setLED(index, color)
        }
    }

    fun strobe(color: Color, duration: Double, strip: LEDs.Strip) {
        val on = Timer.getFPGATimestamp() % duration / duration > 0.5
        solid(if (on) color else Color.kBlack, strip)
    }

    fun breath(c1: Color, c2: Color, strip: LEDs.Strip, duration: Double = 1.0, timestamp: Double = Timer.getFPGATimestamp()) {
        val x = timestamp % duration / duration * 2.0 * Math.PI
        val ratio = (sin(x) + 1.0) / 2.0
        val red = c1.red * (1 - ratio) + c2.red * ratio
        val green = c1.green * (1 - ratio) + c2.green * ratio
        val blue = c1.blue * (1 - ratio) + c2.blue * ratio
        solid(Color(red, green, blue), strip)
    }

    fun rainbow(cycleLength: Double, duration: Double, strip: LEDs.Strip) {
        var x = (1 - Timer.getFPGATimestamp() / duration % 1.0) * 180.0
        val xDiffPerLed = 180.0 / cycleLength
        for (index in strip.indices) {
            x += xDiffPerLed
            x %= 180.0
            LEDs.buffer.setHSV(index, x.toInt(), 255, 255)
        }
    }

    fun wave(c1: Color, c2: Color, cycleLength: Double, duration: Double, strip: LEDs.Strip) {
        val WAVE_EXPONENT = 0.4

        var x = (1 - Timer.getFPGATimestamp() % duration / duration) * 2.0 * Math.PI
        val xDiffPerLed = 2.0 * Math.PI / cycleLength
        for (index in strip.indices) {
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

    fun stripes(colors: List<Color>, length: Int, duration: Double, strip: LEDs.Strip) {
        for (index in strip.indices) {
            var colorIndex = (floor((index - (Timer.getFPGATimestamp() % duration / duration * length * colors.size)) / length) + colors.size).toInt() % colors.size
            colorIndex = colors.size - 1 - colorIndex
            LEDs.buffer.setLED(index, colors[colorIndex])
        }
    }
}