package org.team9432.robot.led.animation

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.coroutineshims.RobotBase
import org.team9432.robot.led.animation.groups.ParallelAnimationGroup
import org.team9432.robot.led.animation.simple.BounceToColor
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.predefined.White
import org.team9432.robot.led.strip.LEDStrip
import org.team9432.robot.led.strip.Sections

object AnimationManager: KPeriodic() {
    private val runningAnimations = mutableSetOf<Animation>()

    private val animationsToSchedule = mutableSetOf<Animation>()
    private val animationsToStop = mutableSetOf<Animation>()

    private var periodicEnabled = false

    override fun periodic() {
        if (periodicEnabled) {
            runningAnimations.forEach { animation ->
                val isFinished = animation.update()
                if (isFinished) stopAnimation(animation)
            }

            animationsToStop.forEach {
                it.end()
                runningAnimations.remove(it)
            }
            animationsToSchedule.forEach {
                it.start()
                runningAnimations.add(it)
            }

            animationsToStop.clear()
            animationsToSchedule.clear()

            LEDStrip.updateColorsFromMap()
            LEDStrip.render()
        }
    }

    fun addAnimation(animation: Animation) {
        animationsToSchedule.add(animation)
    }

    fun stopAnimation(animation: Animation) {
        animationsToStop.add(animation)
    }

    private val loadingAnimation = ParallelAnimationGroup(
        Sections.SPEAKER_LEFT.BounceToColor(Color.White, runReversed = true),
        Sections.SPEAKER_RIGHT.BounceToColor(Color.White),
        Sections.AMP_LEFT.BounceToColor(Color.White, runReversed = true),
        Sections.AMP_RIGHT.BounceToColor(Color.White)
    )

    private var asyncJob: Job? = null

    fun startAsync() {
        periodicEnabled = false
        asyncJob = RobotBase.coroutineScope.launch {
            println("launched")
            loadingAnimation.start()

            while (isActive) {
                delay(20)
                loadingAnimation.update()
                LEDStrip.updateColorsFromMap()
                LEDStrip.render()
            }
            loadingAnimation.end()
        }
    }

    fun stopAsync() {
        asyncJob?.cancel()
        periodicEnabled = true
    }
}