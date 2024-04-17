package org.team9432.robot.led.animation

import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.coroutineshims.RobotBase
import org.team9432.robot.led.animation.groups.ParallelAnimation
import org.team9432.robot.led.animation.simple.BounceToColor
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.PixelColor
import org.team9432.robot.led.color.predefined.White
import org.team9432.robot.led.strip.LEDStrip
import org.team9432.robot.led.strip.Sections

/*
* Each animation defines a priority and the led indices that it uses.
* If an animation with a higher priority is scheduled using some of the indices, it will "cover" the existing animation.
*
* Each running animation has a separate list of the ideal state of its leds at that time.
* The animation manager is responsible for combining those lists based on the priority of the animations being run.
*
* Now hopefully this can be made...
*
* Priorities are integers with lower being higher priority (so you can do the cool thing where -1 is the best :))
*/
object AnimationManager {
    private var periodicEnabled = false

    private val baseStripPixels = LEDStrip.currentColors.toMutableList()

    val animationScope = RobotBase.coroutineScope

    init {
        KCommandScheduler.registerPeriodic(::periodic)
    }

    val callbacks = mutableListOf<AnimationColorCallback>()

    fun periodic() {
        if (!periodicEnabled) return

        // Sort so lower priorities (but higher numbers) are set first and are overwritten later if needed
        callbacks.filterNotNull().map { it.invoke() }.sortedByDescending { (priority, _) -> priority }.forEach { (_, colorset) ->
            // Copy the idealistic state of each animation over to the real strip
            // Skip if the animation doesn't actually affect the leds (usually a group)
            colorset.forEachIndexed { index, pixelColor ->
                if (pixelColor != null) baseStripPixels[index] = pixelColor
            }
        }

        LEDStrip.updateColorsFromMap(baseStripPixels)
        LEDStrip.render()
    }

    private val loadingAnimation = ParallelAnimation(
        RunningAnimation(BounceToColor(Color.White, Sections.SPEAKER_LEFT, runReversed = true)),
        RunningAnimation(BounceToColor(Color.White, Sections.SPEAKER_RIGHT)),
        RunningAnimation(BounceToColor(Color.White, Sections.AMP_LEFT, runReversed = true)),
        RunningAnimation(BounceToColor(Color.White, Sections.AMP_RIGHT))
    )

    fun startAsync() {
//        periodicEnabled = false
//        RobotBase.coroutineScope.launch {
//            println("launched")
//            loadingAnimation.start()
//
//            while (loadingAnimation.isActive) {
//                loadingAnimation.idealisticLedColors.colors.forEachIndexed { index, pixelColor ->
//                    loadingAnimation.section[index] = pixelColor
//                }
//                LEDStrip.updateColorsFromMap()
//                LEDStrip.render()
//                delay(20)
//            }
//
//            periodicEnabled = true
//        }

        periodicEnabled = true
    }
}

typealias AnimationColorCallback = () -> Pair<Int, List<PixelColor?>>