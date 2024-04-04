package org.team9432.robot.led.animations

class ParallelAnimationGroup(vararg animations: Animation): Animation {
    private val animations = animations.toMutableList()

    override fun update(): Boolean {
        val finishedAnimations = mutableListOf<Animation>()
        for (animation in animations) {
            val isFinished = animation.update()
            if (isFinished) finishedAnimations.add(animation)
        }
        animations.removeAll(finishedAnimations)

        return animations.isEmpty()
    }

    override fun start() {
        animations.forEach { it.start() }
    }
}