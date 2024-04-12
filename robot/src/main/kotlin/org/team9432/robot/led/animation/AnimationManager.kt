package org.team9432.robot.led.animation

import edu.wpi.first.wpilibj.Notifier
import org.team9432.LOOP_PERIOD_SECS
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.robot.led.strip.LEDStrip

object AnimationManager: KPeriodic() {
    private val runningAnimations = mutableSetOf<Animation>()

    private val animationsToSchedule = mutableSetOf<Animation>()
    private val animationsToStop = mutableSetOf<Animation>()

    private var periodicEnabled = false

    override fun periodic() {
        if (periodicEnabled) {
            updateAnimations()
        }
    }

    private fun updateAnimations() {
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

    fun addAnimation(animation: Animation) {
        animationsToSchedule.add(animation)
    }

    fun stopAnimation(animation: Animation) {
        animationsToStop.add(animation)
    }


    private val thread = Notifier { updateAnimations() }
    fun startAsync() {
        periodicEnabled = false
        thread.startPeriodic(LOOP_PERIOD_SECS)
    }

    fun stopAsync() {
        thread.stop()
        periodicEnabled = true
    }
}