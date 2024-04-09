package org.team9432.robot.led.animation.simple

import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.strip.Section

fun Section.SlideToColor(color: Color, leadColor: Color = color, runReversed: Boolean = false) = SlideToColor(this, color, leadColor, runReversed)

class SlideToColor(
    private val section: Section,
    private val color: Color,
    private val leadColor: Color = color,
    private val runReversed: Boolean = false,
): Animation {
    private val indicesInOrder = section.indices.toList()

    private var runningIndices = indicesInOrder.toMutableSet()

    override fun start() {
        section.forEachColor { currentlyFadingColor = null }

        if (runReversed) {
            runningIndices = indicesInOrder.reversed().toMutableSet()
        } else {
            runningIndices = indicesInOrder.toMutableSet()
        }
    }

    override fun update(): Boolean {
        val currentPosition = runningIndices.first()
        runningIndices = runningIndices.drop(1).toMutableSet()

        section.revertStrip()

        section.applyToIndex(currentPosition) {
            prolongedColor = color
            temporaryColor = leadColor
        }

        return runningIndices.isEmpty()
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