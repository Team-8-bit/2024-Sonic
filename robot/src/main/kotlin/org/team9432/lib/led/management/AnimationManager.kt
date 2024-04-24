package org.team9432.lib.led.management

import org.team9432.lib.commandbased.KPeriodic
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
object AnimationManager: KPeriodic() {
    /** A list of all the animations that are currently being managed. */
    private val runningAnimations = mutableSetOf<Animation>()

    override fun periodic() {
        handleQueue() // Handle the queue before it starts to avoid concurrent modification

        // Build a new list for each of indices on the strip
        val colorList = List(LEDStrip.ledCount) { index ->
            // Get all animations that want to set the color of this index
            val animationsUsingThisIndex = runningAnimations.filter { it.section.containsBaseStripPixel(index) }

            if (animationsUsingThisIndex.isEmpty()) {
                // If there aren't any animations using this pixel, just use the color it's already set to
                LEDStrip.getPixelColor(index)
            } else {
                // Else take the color from the animation with the highest priority
                animationsUsingThisIndex.maxBy { it.priority ?: 0 }.colorset[index]
            }
        }

        // Update the led strip with the new colors
        LEDStrip.updateColors(colorList)
        // Render!
        LEDStrip.render()
    }

    /** Updates the [runningAnimations] list from queued items. */
    private fun handleQueue() {
        animationRemoveQueue.forEach { runningAnimations.remove(it) }
        animationRemoveQueue.clear()

        animationAddQueue.forEach { runningAnimations.add(it) }
        animationAddQueue.clear()
    }

    /** Animations to add. */
    private val animationAddQueue = mutableSetOf<Animation>()

    /** Animations to remove. */
    private val animationRemoveQueue = mutableSetOf<Animation>()

    /** Adds an animation to the manager. */
    fun addAnimation(animation: Animation) {
        animationAddQueue.add(animation)
    }

    /** Removes an animation from the manager. */
    fun removeAnimation(animation: Animation) {
        animationRemoveQueue.add(animation)
    }
}