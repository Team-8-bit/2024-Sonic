package org.team9432.robot.led.animation.simple

import edu.wpi.first.wpilibj.Timer
import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.strip.Section
import kotlin.math.abs

class FadeToColor(
    private val section: Section,
    private val color: Color,
    private val duration: Double,
    private val fadeSpeed: Int,
): Animation {
    private var initialTimestamp = Timer.getFPGATimestamp()

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