package org.team9432

import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import org.littletonrobotics.junction.LoggedRobot
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.withTimeout
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoChooser
import org.team9432.robot.auto.commands.PullFromSpeakerShooter
import org.team9432.robot.commands.stop
import org.team9432.robot.oi.Controls
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter
import edu.wpi.first.wpilibj2.command.CommandScheduler as WPICommandScheduler
import edu.wpi.first.wpilibj2.command.button.Trigger as WPITrigger

val LOOP_PERIOD_SECS = Robot.period

object Robot: LoggedRobot() {
    override fun robotInit() = Init.initRobot()
    override fun robotPeriodic() = KCommandScheduler.run()
    override fun disabledInit() = KCommandScheduler.cancelAll()

    override fun autonomousInit() {
        ParallelCommand(
            AutoChooser.getCommand(),
            Hood.Commands.aimAtSpeaker()
        ).schedule()
    }

    override fun teleopInit() {
        stop()

        if (RobotState.noteInSpeakerSideHopperBeambreak()) {
            SequentialCommand(
                PullFromSpeakerShooter(),
                InstantCommand { RobotState.hasRemainingAutoNote = true }
            ).withTimeout(0.75).schedule()
        }
    }

    // Use the wpilib command scheduler while in test mode for sysid
    override fun testInit() {
        val tests = Shooter.getSysIdTests()
        WPITrigger { Controls.driver.a.asBoolean }.whileTrue(tests.dynamicForward)
        WPITrigger { Controls.driver.b.asBoolean }.whileTrue(tests.dynamicReverse)
        WPITrigger { Controls.driver.x.asBoolean }.whileTrue(tests.quasistaticForward)
        WPITrigger { Controls.driver.y.asBoolean }.whileTrue(tests.quasistaticReverse)
    }

    override fun testPeriodic() {
        WPICommandScheduler.getInstance().run()
    }
}