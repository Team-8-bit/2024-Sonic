package org.team9432.robot.led.animation.groups

import edu.wpi.first.wpilibj.Timer
import org.team9432.robot.led.animation.Animation

class WaitAnimation(private val time: Double): Animation {
    private val timer = Timer()

    override fun start() = timer.restart()

    override fun update() = timer.hasElapsed(time)

    override fun end() = timer.stop()
}