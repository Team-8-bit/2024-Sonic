package org.team9432.robot.led.animation.simple

import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.strip.Section

fun Section.Solid(color: Color) = Solid(this, color)

class Solid(private val section: Section, private val color: Color): Animation {
    override fun start() {
        section.forEachColor {
            prolongedColor = color
            currentlyFadingColor = null
            temporaryColor = null
        }
    }

    override fun update(): Boolean {
        return true
    }
}