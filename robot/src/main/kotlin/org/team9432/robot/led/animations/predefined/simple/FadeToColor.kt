package org.team9432.robot.led.animations.predefined.simple

import edu.wpi.first.wpilibj.Timer
import org.team9432.robot.led.LEDSection
import org.team9432.robot.led.animations.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.ledinterface.forEachColor
import kotlin.math.abs

class FadeToColor(
    private val section: LEDSection,
    private val color: Color,
    private val fadeSpeed: Int,
    private val duration: Double
): Animation {
    var initialTimestamp = Timer.getFPGATimestamp()

    override fun start() {
        section.forEachColor {
            prolongedColor = color
            currentlyFadingColor = actualColor
            fadeSpeed = this@FadeToColor.fadeSpeed
        }

        initialTimestamp = Timer.getFPGATimestamp()
    }

    override fun update(): Boolean {
        return abs(initialTimestamp - Timer.getFPGATimestamp()) > duration
    }

    override fun end() {
        section.forEachColor { prolongedColor = color }
    }
}