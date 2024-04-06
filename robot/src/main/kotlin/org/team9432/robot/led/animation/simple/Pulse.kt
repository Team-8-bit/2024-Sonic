package org.team9432.robot.led.animation.simple

import edu.wpi.first.wpilibj.Timer
import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.predefined.Black
import org.team9432.robot.led.strip.Section

class Pulse(
    private val section: Section,
    private val color: Color,
    private val duration: Double = 1.0,
    private val cooldown: Double = 1.0,
): Animation {
    override fun start() {
        section.forEachColor {
            prolongedColor = Color.Black
            currentlyFadingColor = Color.Black
            temporaryColor = null
            fadeSpeed = 15
        }
    }

    override fun update(): Boolean {
        val timestamp = Timer.getFPGATimestamp()
        val timeInCurrentCycle = timestamp % (duration + cooldown)

        // Stop if it is in cooldown
        if (timeInCurrentCycle > duration) return false

        // Set one to be lit up in each strip
        val stepTime = duration / section.ledCount
        val position = (timeInCurrentCycle / stepTime).toInt()

        section.applyToIndex(position) { currentlyFadingColor = color }

        return false
    }

    override fun end() {
        section.forEachColor {
            currentlyFadingColor = null
        }
    }
}