package org.team9432.robot.led.animation.simple

import edu.wpi.first.wpilibj.Timer
import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.predefined.Black
import org.team9432.robot.led.strip.Section

class Strobe(
    private val section: Section,
    private val color: Color,
    private val duration: Double,
): Animation {
    override fun start() {
        section.forEachColor {
            prolongedColor = Color.Black
            currentlyFadingColor = null
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