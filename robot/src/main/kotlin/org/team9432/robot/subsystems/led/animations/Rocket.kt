package org.team9432.robot.subsystems.led.animations

import edu.wpi.first.math.MathUtil
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.util.Color
import org.team9432.robot.subsystems.led.LEDModes
import org.team9432.robot.subsystems.led.LEDs

class Rocket(private val duration: Double, private val cooldown: Double = 1.0, private val color: HSVColor? = null): LEDAnimation {
    data class HSVColor(val h: Int, val s: Int, val v: Int)

    private val stepTime = 0.04

    private var timer = Timer()

    private var mainColor = HSVColor(255, 255, 255)
    private var leadColor = HSVColor(255, 255, 255)

    override fun updateBuffer(): Boolean {
        val currentTime = timer.get()
        val currentLength = (currentTime / stepTime).toInt()

        // Start with everything off
        LEDModes.solid(Color.kBlack, LEDs.Section.ALL)

        if (currentTime < duration) {
            sections.forEach { section ->
                var distanceFromLead = currentLength

                section.take(currentLength).forEach {
                    LEDs.buffer.setHSV(it, mainColor.h, mainColor.s, maxOf(mainColor.v - (distanceFromLead * 35), 0))
                    distanceFromLead--
                }
                LEDs.buffer.setHSV(section[MathUtil.clamp(currentLength, 0, 21)], leadColor.h, leadColor.s, leadColor.v)
            }
        }

        return currentTime > (duration + cooldown)
    }

    override fun reset() {
        timer.reset()
        timer.start()

        if (color == null) {
            val toInt = (Math.random() * 3).toInt()

            when (toInt) {
                0 -> mainColor = HSVColor(0, 255, 255)
                1 -> mainColor = HSVColor(120, 255, 255)
                2 -> mainColor = HSVColor(240, 255, 255)
            }
            leadColor = mainColor.copy(s = 100)
        } else {
            mainColor = color
            leadColor = mainColor.copy(s = 100)
        }
    }

    // The sections to include, with indices top to bottom
    private val sections = listOf(
        LEDs.Section.SPEAKER_LEFT,
        LEDs.Section.SPEAKER_RIGHT.reversed(),
        LEDs.Section.AMP_LEFT,
        LEDs.Section.AMP_RIGHT.reversed(),
        LEDs.Section.TOP_BAR,
        LEDs.Section.TOP_BAR.reversed()
    )
}