package org.team9432

import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import org.littletonrobotics.junction.LoggedRobot
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.withTimeout
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoChooser
import org.team9432.robot.auto.commands.PullFromSpeakerShooter
import org.team9432.robot.commands.DefaultCommands
import org.team9432.robot.commands.stop
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
        DefaultCommands.clearDefaultCommands()
        KCommandScheduler.cancelAll()

        WPICommandScheduler.getInstance().enable()
        val controller = CommandXboxController(4)
        val tests = Shooter.getSysIdTests()
        WPITrigger { controller.a().asBoolean }.whileTrue(tests.dynamicForward)
        WPITrigger { controller.b().asBoolean }.whileTrue(tests.dynamicReverse)
        WPITrigger { controller.x().asBoolean }.whileTrue(tests.quasistaticForward)
        WPITrigger { controller.y().asBoolean }.whileTrue(tests.quasistaticReverse)
    }

    override fun testPeriodic() {
        WPICommandScheduler.getInstance().run()
    }

    override fun testExit() {
        WPICommandScheduler.getInstance().disable()
        DefaultCommands.setDefaultCommands()
    }
}