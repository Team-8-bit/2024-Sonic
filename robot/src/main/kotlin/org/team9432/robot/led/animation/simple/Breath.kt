package org.team9432.robot.led.animation.simple

import edu.wpi.first.wpilibj.Timer
import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.getAsRgb
import org.team9432.robot.led.color.predefined.Black
import org.team9432.robot.led.strip.Section

class Breath(
    private val section: Section,
    colors: List<Color>,
    private val colorDuration: Double,
    private val fadeSpeed: Int = 10,
): Animation {

    init {
        assert(colors.isNotEmpty())
    }

    private val colors = colors.map { it.getAsRgb() }
    private var currentColor = 1

    private val colorCount = colors.size

    override fun start() {
        section.forEachColor {
            prolongedColor = colors.first()
            currentlyFadingColor = null
            fadeSpeed = this@Breath.fadeSpeed
        }

        currentColor = 1
        lastColorSet = 1
    }

    private var lastColorSet = 0

    override fun update(): Boolean {
        val timestamp = Timer.getFPGATimestamp()

        val currentTimeInCycle = timestamp % (colorDuration * colorCount)

        val currentColor = (currentTimeInCycle / colorDuration).toInt()
        println(currentColor)

        if (currentColor != lastColorSet) {
            section.forEachColor {
                prolongedColor = colors[currentColor]
                currentlyFadingColor = actualColor
            }
            lastColorSet = currentColor
        }

        return false
    }

    override fun end() {
        section.forEachColor { prolongedColor = Color.Black }
    }
}