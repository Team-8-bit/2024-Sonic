package org.team9432.robot.led.animation.simple

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import org.team9432.lib.delay
import org.team9432.lib.unit.Time
import org.team9432.robot.led.animation.Animation
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.getAsRgb
import org.team9432.robot.led.strip.Section

class Breath(
    colors: List<Color>,
    private val colorDuration: Time,
    private val fadeSpeed: Int = 10,
    override val section: Section,
): Animation() {
    override val colors = section.getColorSet()

    init {
        assert(colors.isNotEmpty())
    }

    private val animationColors = colors.map { it.getAsRgb() }

    override suspend fun runAnimation(scope: CoroutineScope) {
        colors.applyToEach {
            prolongedColor = animationColors.first()
            currentlyFadingColor = null
            fadeSpeed = this@Breath.fadeSpeed
        }

        while (scope.isActive) {
            var currentColor = 0

            while (currentColor < this.animationColors.size) {
                colors.applyToEach {
                    prolongedColor = animationColors[currentColor]
                    currentlyFadingColor = actualColor
                }
                currentColor++
                delay(colorDuration)

                yield()
            }
        }
    }
}
