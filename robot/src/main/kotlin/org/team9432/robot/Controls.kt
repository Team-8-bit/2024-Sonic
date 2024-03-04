package org.team9432.robot


import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.lib.commandbased.commands.withTimeout
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.lib.wpilib.ChassisSpeeds
import org.team9432.robot.commands.drivetrain.FieldOrientedDrive
import org.team9432.robot.commands.intake.Outtake
import org.team9432.robot.commands.intake.TeleIntake
import org.team9432.robot.commands.shooter.ShootStatic
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.led.LEDCommands

object Controls {
    private val controller = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.075)

    private val xSpeedSupplier = { -controller.leftY }
    private val ySpeedSupplier = { -controller.leftX }
    private val angleSupplier = { -controller.rightX }

    init {
        Drivetrain.defaultCommand = FieldOrientedDrive()

        /* --------------- REAL BUTTONS --------------- */

        // Run Intake
        controller.leftBumper
            .whileTrue(TeleIntake().afterSimDelay(2.0) { BeambreakIOSim.setNoteInIntakeSide(RobotState.getMovementDirection(), true) }) // Pretend to get a note after 2 seconds in sim
            .onFalse(CommandIntake.stop()) // This should be blocked if the intake is still aligning the note

        // Outtake Intake
        controller.x.whileTrue(Outtake())

        // Shoot Speaker
        controller.rightTrigger.onTrue(ShootStatic(6000.0, 6000.0).withTimeout(10.0))

        // Shoot Amplifier
        controller.leftTrigger.onTrue(ShootStatic(2500.0, 2500.0).withTimeout(10.0))

        // Reset Drivetrain Heading
        controller.a.onTrue(InstantCommand { Drivetrain.resetGyro() })


        /* --------------- TEST BUTTONS --------------- */

        // Clear note position
        controller.y.onTrue(InstantCommand {
            BeambreakIOSim.setNoteInIntakeAmpSide(false)
            BeambreakIOSim.setNoteInIntakeSpeakerSide(false)
            BeambreakIOSim.setNoteInHopperAmpSide(false)
            BeambreakIOSim.setNoteInHopperSpeakerSide(false)
            BeambreakIOSim.setNoteInCenter(false)
            RobotState.notePosition = RobotState.NotePosition.NONE
        })

        // Raise Climbers
        controller.start.onTrue(InstantCommand(LeftClimber, RightClimber) {
            LeftClimber.setVoltage(6.0)
            RightClimber.setVoltage(6.0)
        }).onFalse(InstantCommand(LeftClimber, RightClimber) {
            LeftClimber.stop()
            RightClimber.stop()
        })

        // Lower Climbers
        controller.back.onTrue(InstantCommand(LeftClimber, RightClimber) {
            LeftClimber.setVoltage(-6.0)
            RightClimber.setVoltage(-6.0)
        }).onFalse(InstantCommand(LeftClimber, RightClimber) {
            LeftClimber.stop()
            RightClimber.stop()
        })

        // Test LEDs
        controller.b.whileTrue(LEDCommands.testMode())
    }

    fun getDrivetrainSpeeds(): ChassisSpeeds {
        val maxSpeedMetersPerSecond = if (controller.rightBumper.asBoolean) 6.0 else 2.5
        val xSpeed = xSpeedSupplier.invoke() * maxSpeedMetersPerSecond
        val ySpeed = ySpeedSupplier.invoke() * maxSpeedMetersPerSecond
        val radiansPerSecond = Math.toRadians(angleSupplier.invoke() * 360.0)
        return ChassisSpeeds(xSpeed, ySpeed, radiansPerSecond)
    }
}
