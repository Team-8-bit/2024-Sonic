package org.team9432.robot.led.animation.simple

import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.predefined.Black
import org.team9432.robot.led.strip.Section

fun Section.BounceToColor(color: Color, leadColor: Color = color, initialColor: Color? = null, runReversed: Boolean = false) = BounceToColor(this, color, leadColor, initialColor, runReversed)

class BounceToColor(
    private val section: Section,
    private val color: Color,
    private val leadColor: Color = color,
    private val initialColor: Color? = null,
    private val runReversed: Boolean = false,
): Animation {
    override fun start() {
        section.forEachColor {
            initialColor?.let { prolongedColor = it }
            currentlyFadingColor = null
        }

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

        if (minPosition == maxPosition) {
            return true
        }

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
