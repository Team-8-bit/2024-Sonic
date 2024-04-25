package org.team9432.robot.oi


import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.GenericHID
import org.team9432.lib.commandbased.commands.*
import org.team9432.lib.commandbased.input.KTrigger
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.FieldConstants
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CheckIfNoteIsOuttaked
import org.team9432.robot.commands.amp.OuttakeAmp
import org.team9432.robot.commands.amp.ScoreAmp
import org.team9432.robot.commands.drivetrain.teleop.TeleAngleDrive
import org.team9432.robot.commands.intake.TeleIntake
import org.team9432.robot.commands.shooter.FeedNote
import org.team9432.robot.commands.shooter.OuttakeShooter
import org.team9432.robot.commands.shooter.Subwoofer
import org.team9432.robot.commands.shooter.TeleShootMultiple
import org.team9432.robot.commands.stopCommand
import org.team9432.robot.oi.switches.DSSwitches
import org.team9432.robot.sensors.beambreaks.BeambreakIOSim
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.subsystems.Superstructure

object Controls {
    private val driver = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.075)

    private val slowButton = driver.rightBumper
    private val readyToShootSpeakerButton = driver.b
    private val readyToShootAmpButton = driver.b

    val xSpeed get() = -driver.leftY
    val ySpeed get() = -driver.leftX
    val angle get() = -driver.rightX
    val slowDrive get() = slowButton()

    val readyToShootSpeaker get() = readyToShootSpeakerButton()
    val readyToShootAmp get() = readyToShootAmpButton()

    val upPov = KTrigger { driver.getPOV(0) == 0 }
    val downPov = KTrigger { driver.getPOV(0) == 180 }
    val leftPov = KTrigger { driver.getPOV(0) == 270 }
    val rightPov = KTrigger { driver.getPOV(0) == 90 }

    fun setButtons() {
        // Run Intake
        driver.leftBumper
            .whileTrue(TeleIntake().afterSimDelay(2.0) {
                BeambreakIOSim.setNoteInIntakeSide(RobotState.getMovementDirection(), true)
            }) // Pretend to get a note after 2 seconds in sim

        // Outtake Intake
        driver.x.whileTrue(Superstructure.Commands.runOuttake())

        upPov.whileTrue(Superstructure.Commands.runOuttakeAmpFix())
        downPov.whileTrue(Superstructure.Commands.runOuttakeSpeakerFix())
        leftPov.whileTrue(OuttakeAmp()).onFalse(CheckIfNoteIsOuttaked())
        rightPov.whileTrue(OuttakeShooter()).onFalse(CheckIfNoteIsOuttaked())

        // Shoot Speaker
        driver.rightTrigger
            .whileTrue(SuppliedCommand {
                if (DSSwitches.shouldUseAmpForSpeaker) ScoreAmp(12.0)
                else if (DSSwitches.teleAutoAimDisabled) Subwoofer()
                else TeleShootMultiple()
            })

        // Aim at the speaker
        driver.a
            .whileTrue(
                ParallelDeadlineCommand(
                    TeleAngleDrive { FieldConstants.feedPose.rotation },
                    deadline = FeedNote()
                )
            )

        // Reset Drivetrain Heading
        driver.start
            .onTrue(InstantCommand { Gyro.resetYaw() }.runsWhenDisabled(true))

        // Reset
        driver.y
            .onTrue(stopCommand())

        // Score amp
        driver.leftTrigger
            .onTrue(
                SuppliedCommand {
                    if (DSSwitches.teleAutoAimDisabled) ScoreAmp(4.75)
                    else ParallelDeadlineCommand(
                        TeleAngleDrive { Rotation2d.fromDegrees(-90.0) },
                        deadline = ScoreAmp(4.75)
                    )
                }
            )
    }

    fun setDriverRumble(magnitude: Double) {
        if (DriverStation.isTeleopEnabled())
            driver.setRumble(GenericHID.RumbleType.kBothRumble, magnitude)
    }
}
