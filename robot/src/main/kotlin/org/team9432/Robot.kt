package org.team9432

import org.littletonrobotics.junction.LoggedCoroutineRobot
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.withTimeout
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoChooser
import org.team9432.robot.auto.commands.PullFromSpeakerShooter
import org.team9432.robot.commands.stop
import org.team9432.robot.oi.Controls

object Robot: LoggedCoroutineRobot() {
    var hasBeenEnabled = false

    override fun robotInit() = Init.initRobot()
    override fun robotPeriodic() = KCommandScheduler.run()

    override fun disabledInit() {
        Controls.setDriverRumble(0.0)
        KCommandScheduler.cancelAll()
    }

    override fun autonomousInit() {
        SequentialCommand(
            PullFromSpeakerShooter(),
            AutoChooser.getCommand(),
        ).schedule()
    }

    override fun teleopInit() {
        stop()

        if (RobotState.noteInSpeakerSideHopperBeambreak()) {
            SequentialCommand(
                PullFromSpeakerShooter(),
                InstantCommand { RobotState.notePosition = RobotState.NotePosition.SPEAKER_HOPPER }
            ).withTimeout(0.75).schedule()
        }
    }

    override fun disabledExit() {
        hasBeenEnabled = true
    }
}