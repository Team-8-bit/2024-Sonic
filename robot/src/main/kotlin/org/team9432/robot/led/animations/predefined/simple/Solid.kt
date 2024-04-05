package org.team9432.robot.led.animations.predefined.simple

import org.team9432.robot.led.LEDSection
import org.team9432.robot.led.animations.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.ledinterface.forEachColor

class Solid(private val section: LEDSection, private val color: Color): Animation {
    override fun start() {
        section.forEachColor {
            prolongedColor = color
            fadeColor = null
            temporaryColor = null
        }
    }

    override fun update(): Boolean {
        return true
    }
}