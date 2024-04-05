package org.team9432.robot.led.animation.groups

import org.team9432.robot.led.animation.Animation

class SequentialAnimationGroup(vararg animations: Animation): Animation {
    private val animationList = animations.toList()

    private var unfinishedAnimations = animationList.toMutableList()

    override fun start() {
        unfinishedAnimations = animationList.toMutableList()
        unfinishedAnimations.first().start()
    }

    override fun update(): Boolean {
        val animation = unfinishedAnimations.first()
        val isFinished = animation.update()
        if (isFinished) {
            animation.end()
            unfinishedAnimations.remove(animation)

            unfinishedAnimations.firstOrNull()?.start()
        }

        return unfinishedAnimations.isEmpty()
    }

    override fun end() {
        // End any interrupted animations
        if (unfinishedAnimations.isNotEmpty()) {
            unfinishedAnimations.first().end()
        }
    }
}