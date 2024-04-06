package org.team9432.robot.led.animation.groups

import org.team9432.robot.led.animation.Animation

class ParallelAnimationGroup(vararg animations: Animation): Animation {
    private val animationList = animations.toList()

    private var unfinishedAnimations = animationList.toMutableList()

    override fun start() {
        unfinishedAnimations = animationList.toMutableList()
        unfinishedAnimations.forEach { it.start() }
    }

    override fun update(): Boolean {
        val finishedAnimations = mutableListOf<Animation>()
        for (animation in unfinishedAnimations) {
            val isFinished = animation.update()
            if (isFinished) finishedAnimations.add(animation)
        }
        finishedAnimations.forEach { it.end() }
        unfinishedAnimations.removeAll(finishedAnimations)

        return unfinishedAnimations.isEmpty()
    }

    override fun end() {
        // End any remaining animations
        unfinishedAnimations.forEach { it.end() }
    }
}