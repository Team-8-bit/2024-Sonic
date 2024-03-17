package org.team9432.robot.subsystems.led.animations

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.util.Color
import org.team9432.robot.subsystems.led.LEDModes
import org.team9432.robot.subsystems.led.LEDs

class Confetti(private val duration: Double): LEDAnimation {

    private val stepTime = 0.5

    private var timer = Timer()

    override fun updateBuffer(): Boolean {
        val currentTime = timer.get()
        val currentConfettiLength = (currentTime / stepTime).toInt()

        // Start with everything white
        LEDModes.solid(Color.kWhite, LEDs.Section.ALL)
        sections.forEach { section ->
            // Take the indices up to the current step
            section.take(currentConfettiLength).forEach {
                if (Math.random() > 0.4) {
                    val color = (Math.random() * 3).toInt()
                    when (color) {
                        0 -> LEDs.buffer.setRGB(it, 255, 0, 0)
                        1 -> LEDs.buffer.setRGB(it, 0, 255, 0)
                        2 -> LEDs.buffer.setRGB(it, 0, 0, 255)
                    }
                }
            }
        }

        return currentTime > duration
    }

    override fun reset() {
        timer.reset()
        timer.start()
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