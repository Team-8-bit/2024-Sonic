package org.team9432.robot.subsystems.led.animations

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.util.Color
import org.team9432.robot.subsystems.led.LEDModes
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.LEDs

class Rocket(private val duration: Double, private val cooldown: Double = 1.0) : LEDAnimation {
    data class HSVColor(val h: Int, val s: Int, val v: Int)

    private val stepTime = 0.1

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
                    LEDs.buffer.setHSV(it, mainColor.h, mainColor.s, mainColor.v - ((currentLength * 15) + (distanceFromLead * 10)))
                    distanceFromLead--
                }
                LEDs.buffer.setHSV(section[currentLength], leadColor.h, leadColor.s, leadColor.v - (currentLength * 15))
            }
        }

        return currentTime > (duration + cooldown)
    }

    override fun reset() {
        timer.reset()
        timer.start()

        val toInt = (Math.random() * 3).toInt()
        println(toInt)

        when(toInt) {
            0 -> {
                mainColor = HSVColor(0, 255, 255)
                leadColor = HSVColor(0, 100, 255)
            }
            1 -> {
                mainColor = HSVColor(120, 255, 255)
                leadColor = HSVColor(120, 100, 255)
            }
            2 -> {
                mainColor = HSVColor(240, 255, 255)
                leadColor = HSVColor(240, 100, 255)
            }
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