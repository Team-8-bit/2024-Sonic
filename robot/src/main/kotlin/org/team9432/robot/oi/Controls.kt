package org.team9432.robot.oi


import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.GenericHID
import org.team9432.lib.commandbased.commands.*
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.FieldConstants
import org.team9432.robot.RobotState
import org.team9432.robot.commands.amp.ScoreAmp
import org.team9432.robot.commands.bazooka.ApplyBazooka
import org.team9432.robot.commands.bazooka.BazookaAlignmentTest
import org.team9432.robot.commands.drivetrain.teleop.TeleAngleDrive
import org.team9432.robot.commands.drivetrain.teleop.TeleTargetDrive
import org.team9432.robot.commands.hopper.MoveToPosition
import org.team9432.robot.commands.intake.TeleIntake
import org.team9432.robot.commands.shooter.TeleShootMultiple
import org.team9432.robot.commands.stopCommand
import org.team9432.robot.oi.switches.DSSwitches
import org.team9432.robot.sensors.beambreaks.BeambreakIOSim
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.subsystems.Superstructure

object Controls {
    private val driver = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.075)
    private val test = KXboxController(1, squareJoysticks = true, joystickDeadband = 0.075)

    private val slowButton = driver.rightBumper
    private val readyToShootSpeakerButton = driver.b
    private val readyToShootAmpButton = driver.b

    val xSpeed get() = -driver.leftY
    val ySpeed get() = -driver.leftX
    val angle get() = -driver.rightX
    val slowDrive get() = slowButton.asBoolean

    val readyToShootSpeaker get() = readyToShootSpeakerButton.asBoolean
    val readyToShootAmp get() = readyToShootAmpButton.asBoolean

    fun setButtons() {
        // Run Intake
        driver.leftBumper
            .whileTrue(TeleIntake().afterSimDelay(2.0) {
                BeambreakIOSim.setNoteInIntakeSide(RobotState.getMovementDirection(), true)
            }) // Pretend to get a note after 2 seconds in sim

        // Outtake Intake
        driver.x
            .whileTrue(Superstructure.Commands.runOuttake())

        // Shoot Speaker
        driver.rightTrigger
            .whileTrue(SuppliedCommand {
                if (DSSwitches.shouldUseAmpForSpeaker) ScoreAmp(12.0)
                else TeleShootMultiple()
            })

        // Aim at the speaker
        driver.a
            .whileTrue(TeleTargetDrive { FieldConstants.speakerAimPose })

        // Reset Drivetrain Heading
        driver.start
            .onTrue(InstantCommand { Gyro.resetYaw() }.runsWhenDisabled(true))

        driver.back
            .onTrue(ApplyBazooka())

        // Reset
        driver.y
            .onTrue(stopCommand())

        // Score amp
        driver.leftTrigger
            .onTrue(
                ParallelDeadlineCommand(
                    TeleAngleDrive { Rotation2d.fromDegrees(-90.0) },
                    deadline = ScoreAmp(4.5)
                )
            )

        test.a.onTrue(MoveToPosition(RobotState.NotePosition.AMP_INTAKE))
        test.b.onTrue(MoveToPosition(RobotState.NotePosition.SPEAKER_INTAKE))
        test.x.onTrue(MoveToPosition(RobotState.NotePosition.AMP_HOPPER))
        test.y.onTrue(MoveToPosition(RobotState.NotePosition.SPEAKER_HOPPER))
    }

    fun setDriverRumble(magnitude: Double) {
        driver.setRumble(GenericHID.RumbleType.kBothRumble, magnitude)
    }
}
