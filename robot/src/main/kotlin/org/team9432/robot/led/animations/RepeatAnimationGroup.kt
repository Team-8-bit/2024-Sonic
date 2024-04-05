package org.team9432.robot.led.animations

class RepeatAnimationGroup(private val animation: Animation, private val runs: Int? = null) : Animation {
    private var runCount = 0

    override fun start() {
        runCount = 0
        animation.start()
    }

    override fun update(): Boolean {
        val isFinished = animation.update()
        if (isFinished) {
            animation.end()
            runCount++
        }
        val isPassedRunCount = runs?.let { runCount > it } ?: false
        if (!isPassedRunCount && isFinished) animation.start()
        return isPassedRunCount
    }

    override fun end() {
        // Call end if the animation was interrupted
        val isPassedRunCount = runs?.let { runCount > it } ?: false
        if (!isPassedRunCount) animation.end()
    }
}