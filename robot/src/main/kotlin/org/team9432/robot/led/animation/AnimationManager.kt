package org.team9432.robot.led.animation

import edu.wpi.first.wpilibj.Notifier
import org.team9432.LOOP_PERIOD_SECS
import org.team9432.lib.commandbased.KPeriodic
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

    private val thread = Notifier {
        loadingAnimation.update()
        LEDStrip.updateColorsFromMap()
        LEDStrip.render()
    }

    fun startAsync() {
        periodicEnabled = false
        thread.startPeriodic(LOOP_PERIOD_SECS)
    }

    fun stopAsync() {
        thread.stop()
        periodicEnabled = true
    }
}