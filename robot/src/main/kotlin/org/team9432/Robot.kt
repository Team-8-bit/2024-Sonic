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

val LOOP_PERIOD_SECS = Robot.period

object Robot: LoggedRobot() {
    override fun robotInit() = Init.initRobot()
    override fun robotPeriodic() = KCommandScheduler.run()
    override fun disabledInit() = KCommandScheduler.cancelAll()

    override fun autonomousInit() {
        ParallelCommand(
            AutoChooser.getCommand(),
            Hood.Commands.aimAtSpeaker(),
            Shooter.Commands.runAtSpeeds()
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

    init {
        val controller = CommandXboxController(4)
        val tests = Hood.getSysIdTests()
        controller.a().whileTrue(tests.dynamicForward)
        controller.b().whileTrue(tests.dynamicReverse)
        controller.x().whileTrue(tests.quasistaticForward)
        controller.y().whileTrue(tests.quasistaticReverse)
    }

    // Use the wpilib command scheduler while in test mode for sysid
    override fun testInit() {
        KCommandScheduler.disable()
        DefaultCommands.clearDefaultCommands()
        KCommandScheduler.enable()

        KCommandScheduler.cancelAll()

        WPICommandScheduler.getInstance().enable()
    }

    override fun testPeriodic() {
        WPICommandScheduler.getInstance().run()
    }

    override fun testExit() {
        WPICommandScheduler.getInstance().disable()

        KCommandScheduler.disable()
        DefaultCommands.setDefaultCommands()
        KCommandScheduler.enable()
    }
}