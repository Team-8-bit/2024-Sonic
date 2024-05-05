package org.team9432.lib.commandbased.commands

import edu.wpi.first.wpilibj.Timer
import org.team9432.lib.commandbased.KCommand

/** A command that does nothing but takes a specified amount of time to finish. */
class WaitCommand(private val duration: Double): KCommand() {
    override var runsWhenDisabled = true
    private val timer: Timer = Timer()

    override fun initialize() {
        timer.restart()
    }

    override fun end(interrupted: Boolean) {
        timer.stop()
    }

    override fun isFinished(): Boolean {
        return timer.hasElapsed(duration)
    }
}