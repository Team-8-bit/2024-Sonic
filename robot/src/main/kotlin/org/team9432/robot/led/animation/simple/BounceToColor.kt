package org.team9432.robot.led.animation.simple

import edu.wpi.first.wpilibj.Timer
import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.predefined.Black
import org.team9432.robot.led.strip.Section

class BounceToColor(
    private val section: Section,
    private val color: Color,
    private val leadColor: Color = color,
    private val initialColor: Color = Color.Black,
    private val runReversed: Boolean = false,
): Animation {
    private var initialTimestamp = Timer.getFPGATimestamp()

    override fun start() {
        section.forEachColor {
            prolongedColor = initialColor
            currentlyFadingColor = null
        }

        initialTimestamp = Timer.getFPGATimestamp()

        if (runReversed) {
            maxPosition = section.indices.last
            minPosition = section.indices.first
            currentPosition = maxPosition
            currentDirection = -1
        } else {
            maxPosition = section.indices.last
            minPosition = section.indices.first
            currentPosition = minPosition
            currentDirection = 1
        }
    }

    private var currentPosition = 0

    // 1 or -1
    private var currentDirection = 1

    private var maxPosition = section.indices.last
    private var minPosition = section.indices.first

    override fun update(): Boolean {
        currentPosition += currentDirection

        if (minPosition == maxPosition) { return true }

        if (currentPosition == maxPosition && currentDirection == 1) {
            section.applyToIndex(maxPosition) { prolongedColor = color }
            maxPosition--
            currentDirection = -1
        } else if (currentPosition == minPosition && currentDirection == -1) {
            section.applyToIndex(minPosition) { prolongedColor = color }
            minPosition++
            currentDirection = 1
        }

        section.revertStrip()

        section.applyToIndex(currentPosition) { temporaryColor = leadColor }

        return false
    }

    override fun end() {
        section.forEachColor {
            prolongedColor = color
            actualColor = color
            currentlyFadingColor = null
            temporaryColor = null
        }
    }
}