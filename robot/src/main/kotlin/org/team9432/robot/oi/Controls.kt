package org.team9432.robot.oi


import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.GenericHID
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.lib.commandbased.commands.runsWhenDisabled
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.RobotState
import org.team9432.robot.commands.amp.ScoreAmp
import org.team9432.robot.commands.drivetrain.teleop.TeleDrive
import org.team9432.robot.commands.intake.Outtake
import org.team9432.robot.commands.intake.TeleIntake
import org.team9432.robot.commands.shooter.SubwooferShoot
import org.team9432.robot.commands.shooter.TeleShoot
import org.team9432.robot.commands.stopCommand
import org.team9432.robot.sensors.beambreaks.BeambreakIOSim
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.subsystems.climber.CommandClimber
import org.team9432.robot.subsystems.drivetrain.Drivetrain

object Controls {
    private val driver = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.075)
    private val operator = KXboxController(1)

    private val slowButton = driver.rightBumper

    val xSpeed get() = -driver.leftY
    val ySpeed get() = -driver.leftX
    val angle get() = -driver.rightX
    val slowDrive get() = slowButton.asBoolean

    fun setButtons() {
        /* -------------- DRIVER -------------- */

        // Run Intake
        driver.leftBumper
            .whileTrue(TeleIntake().afterSimDelay(2.0) {
                BeambreakIOSim.setNoteInIntakeSide(RobotState.getMovementDirection(), true)
            }) // Pretend to get a note after 2 seconds in sim

        // Outtake Intake
        driver.x
            .whileTrue(Outtake())

        // Shoot Speaker
        driver.rightTrigger
            .onTrue(SuppliedCommand {
                if (EmergencySwitches.isAmpForSpeaker) ScoreAmp(12.0)
                else if (EmergencySwitches.isSubwooferOnly) SubwooferShoot()
                else TeleShoot()
            })

        // Shoot Amplifier from speaker
        driver.b
            .onTrue(InstantCommand { Gyro.setYaw(Rotation2d(Math.PI)) }.runsWhenDisabled(true))

        // Reset Drivetrain Heading
        driver.a
            .onTrue(InstantCommand { Gyro.resetYaw() }.runsWhenDisabled(true))

        // Reset
        driver.y
            .onTrue(stopCommand())

        // Load to amp
        driver.leftTrigger
            .onTrue(ScoreAmp(4.5))


        /* -------------- OPERATOR -------------- */

        // Raise Left Climber
        operator.leftBumper
            .whileTrue(CommandClimber.runLeftClimber(9.0))

        // Lower Left Climber
        operator.leftTrigger
            .whileTrue(CommandClimber.runLeftClimber(-12.0))

        // Raise Right Climber
        operator.rightBumper
            .whileTrue(CommandClimber.runRightClimber(12.0))

        // Lower Right Climber
        operator.rightTrigger
            .whileTrue(CommandClimber.runRightClimber(-12.0))

        // Raise Both Climbers
        operator.y
            .whileTrue(CommandClimber.runClimbers(12.0))

        // Lower Both Climbers
        operator.a
            .whileTrue(CommandClimber.runClimbers(-12.0))
    }

    fun setDriverRumble(magnitude: Double) {
        driver.setRumble(GenericHID.RumbleType.kBothRumble, magnitude)
    }
}
