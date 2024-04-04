package org.team9432.robot.led.animations.predefined.simple

import edu.wpi.first.wpilibj.Timer
import org.team9432.robot.led.LEDSection
import org.team9432.robot.led.animations.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.presets.*
import org.team9432.robot.led.ledinterface.forEachColor

class Strobe(
    private val section: LEDSection,
    private val color: Color,
    private val duration: Double,
): Animation {
    override fun start() {
        section.forEachColor {
            prolongedColor = Color.Black
            fadeColor = null
            temporaryColor = null
        }
    }

    override fun update(): Boolean {
        val on = Timer.getFPGATimestamp() % duration / duration > 0.5
        val colorToSet = if (on) color else Color.Black

        section.forEachColor { prolongedColor = colorToSet }

        return false
    }

    override fun end() {
        section.forEachColor { prolongedColor = Color.Black }
    }
}