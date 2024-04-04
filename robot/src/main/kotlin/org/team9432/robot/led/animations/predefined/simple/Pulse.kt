package org.team9432.robot.led.animations.predefined.simple

import edu.wpi.first.wpilibj.Timer
import org.team9432.robot.led.LEDSection
import org.team9432.robot.led.LEDs
import org.team9432.robot.led.animations.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.presets.Black
import org.team9432.robot.led.ledinterface.applyToIndex
import org.team9432.robot.led.ledinterface.forEachColor

class Pulse(
    private val section: LEDSection,
    private val color: Color,
    private val duration: Double = 1.0,
    private val cooldown: Double = 1.0,
): Animation {
    override fun start() {
        section.forEachColor {
            prolongedColor = Color.Black
            fadeColor = Color.Black
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

        section.applyToIndex(position) { fadeColor = color }

        return false
    }

    override fun end() {
        section.forEachColor {
            fadeColor = null
        }
    }
}