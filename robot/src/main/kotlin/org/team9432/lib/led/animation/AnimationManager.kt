package org.team9432.lib.led.animation

import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.coroutineshims.RobotBase
import org.team9432.lib.led.strip.LEDStrip

/**
 * Each animation defines a priority and the led indices that it uses.
 *
 * Each time the strip attempts to "render" the current pattern, it finds the highest
 * priority animation using that index and takes the color from there.
 *
 * If an animation with a higher priority is scheduled using some of the indices,
 * it will "cover" the existing animation.
 *
 * Each running animation has a separate list of the ideal state of its leds at that time.
 * The animation manager is responsible for combining those lists based on the priority
 * of the animations being run.
 *
 * Priorities are integers (defaulting to zero) with higher numbers being higher priority.
 */
object AnimationManager {
    val animationScope = RobotBase.coroutineScope

    init {
        KCommandScheduler.registerPeriodic(::periodic)
    }

    private val runningAnimations = mutableSetOf<Animation>()

    val animationAddQueue = mutableSetOf<Animation>()
    val animationRemoveQueue = mutableSetOf<Animation>()

    fun periodic() {
        animationRemoveQueue.forEach { runningAnimations.remove(it) }
        animationRemoveQueue.clear()

        animationAddQueue.forEach { runningAnimations.add(it) }
        animationAddQueue.clear()

        val newList = List(LEDStrip.getInstance().strip.ledCount) { index ->
            val animationsUsingThisIndex = runningAnimations.filter { it.section.containsBaseStripPixel(index) }

            if (animationsUsingThisIndex.isNotEmpty()) {
                animationsUsingThisIndex.maxBy { it.priority ?: 0 }.colors[index]
            } else { // If there aren't any animations using this pixel, just use the color it's already set to
                LEDStrip.getInstance().currentPixelColors[index]
            }
        }

        LEDStrip.getInstance().updateColorsFromMap(newList)
        LEDStrip.getInstance().render()
    }
}