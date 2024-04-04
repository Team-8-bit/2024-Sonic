package org.team9432.robot.led

import org.team9432.lib.commandbased.KPeriodic
import org.team9432.robot.led.animations.Animation

object AnimationManager: KPeriodic() {
    private val runningAnimations = mutableSetOf<Animation>()

    private val animationsToSchedule = mutableSetOf<Animation>()
    private val animationsToStop = mutableSetOf<Animation>()

    override fun periodic() {
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

        runningAnimations.forEach { animation ->
            val isFinished = animation.update()
            if (isFinished) stopAnimation(animation)
        }
    }

    fun addAnimation(animation: Animation) {
        animationsToSchedule.add(animation)
    }

    fun stopAnimation(animation: Animation) {
        animationsToStop.add(animation)
    }
}