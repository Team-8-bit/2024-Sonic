package org.team9432.robot.led.animation.simple

import kotlinx.coroutines.CoroutineScope
import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.strip.Section

class Solid(private val color: Color, override val section: Section): Animation() {
    override val colors = section.getColorSet()

    override suspend fun runAnimation(scope: CoroutineScope) {
        colors.applyToEach {
            resetToDefault()
            prolongedColor = color
        }
    }
}