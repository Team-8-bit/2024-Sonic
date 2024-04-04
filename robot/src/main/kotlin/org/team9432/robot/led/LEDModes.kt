package org.team9432.robot.led

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.util.Color
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin

object LEDModes {
//    val sidePulses = listOf(
//        LEDs.Section.SPEAKER_LEFT,
//        LEDs.Section.SPEAKER_RIGHT.reversed(),
//        LEDs.Section.AMP_LEFT,
//        LEDs.Section.AMP_RIGHT.reversed()
//    )

//    fun breath(c1: Color, c2: Color, indices: List<Int>, duration: Double = 1.0, timestamp: Double = Timer.getFPGATimestamp()) {
//        val x = timestamp % duration / duration * 2.0 * Math.PI
//        val ratio = (sin(x) + 1.0) / 2.0
//        val red = c1.red * (1 - ratio) + c2.red * ratio
//        val green = c1.green * (1 - ratio) + c2.green * ratio
//        val blue = c1.blue * (1 - ratio) + c2.blue * ratio
//        solid(Color(red, green, blue), indices)
//    }
//
//    fun rainbow(cycleLength: Double, duration: Double, indices: List<Int>) {
//        var x = (1 - Timer.getFPGATimestamp() / duration % 1.0) * 180.0
//        val xDiffPerLed = 180.0 / cycleLength
//        for (index in indices) {
//            x += xDiffPerLed
//            x %= 180.0
//            LEDs.buffer.setHSV(index, x.toInt(), 255, 255)
//        }
//    }

}