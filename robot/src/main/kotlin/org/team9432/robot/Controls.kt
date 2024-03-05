package org.team9432.robot


import edu.wpi.first.math.geometry.Rotation2d
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
import org.team9432.robot.subsystems.hood.CommandHood
import org.team9432.robot.subsystems.intake.CommandIntake

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
        controller.rightTrigger.onTrue(ShootStatic(6000.0, 8000.0).withTimeout(10.0))

        // Shoot Amplifier
        controller.leftTrigger.onTrue(ShootStatic(2500.0, 2500.0).withTimeout(10.0))

        // Reset Drivetrain Heading
        controller.a.onTrue(InstantCommand { Drivetrain.resetGyro() })

        controller.y.onTrue(CommandHood.setAngle(Rotation2d.fromDegrees(0.0)))
        controller.b.onTrue(CommandHood.setAngle(Rotation2d.fromDegrees(15.0)))
        controller.start.onTrue(CommandHood.setAngle(Rotation2d.fromDegrees(30.0)))


        /* --------------- TEST BUTTONS --------------- */
    }

    fun getDrivetrainSpeeds(): ChassisSpeeds {
        val maxSpeedMetersPerSecond = if (controller.rightBumper.asBoolean) 6.0 else 2.5
        val xSpeed = xSpeedSupplier.invoke() * maxSpeedMetersPerSecond
        val ySpeed = ySpeedSupplier.invoke() * maxSpeedMetersPerSecond
        val radiansPerSecond = Math.toRadians(angleSupplier.invoke() * 360.0)
        return ChassisSpeeds(xSpeed, ySpeed, radiansPerSecond)
    }
}
