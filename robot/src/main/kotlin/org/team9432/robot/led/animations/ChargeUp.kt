package org.team9432.robot.led.animations

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.util.Color
import org.team9432.robot.led.LEDModes
import org.team9432.robot.led.LEDs

class ChargeUp(private val duration: Double, private val extraEndTime: Double): LEDAnimation {
    private val steps = 22
    private val stepTime = duration / steps

    private var timer = Timer()

    override fun updateBuffer(): Boolean {
        val currentTime = timer.get()
        val currentStep = (currentTime / stepTime).toInt()

        // Start with everything off
        LEDModes.solid(Color.kBlack, LEDs.Section.ALL)
        sections.forEach { section ->
            // Take the indices up to the current step
            section.take(currentStep)
                // Set them each to white
                .forEach { LEDs.buffer.setLED(it, Color.kWhite) }
        }

        return currentTime > duration + extraEndTime
    }

    override fun reset() {
        timer.reset()
        timer.start()
    }

    // The sections to include, with indices bottom to top
    private val sections = listOf(
        LEDs.Section.SPEAKER_LEFT.reversed(),
        LEDs.Section.SPEAKER_RIGHT,
        LEDs.Section.AMP_LEFT.reversed(),
        LEDs.Section.AMP_RIGHT
    )
}