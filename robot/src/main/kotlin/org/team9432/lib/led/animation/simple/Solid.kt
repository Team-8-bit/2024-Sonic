package org.team9432.lib.led.animation.simple

import kotlinx.coroutines.CoroutineScope
import org.team9432.lib.led.animation.Animation
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.Section

class Solid(private val color: Color, val section: Section): Animation(section) {
    override suspend fun runAnimation(scope: CoroutineScope) {
        colors.applyToEach {
            resetToDefault()
            prolongedColor = color
        }
    }
}