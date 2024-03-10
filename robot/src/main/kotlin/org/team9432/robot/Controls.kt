package org.team9432.robot


import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.lib.commandbased.commands.runsWhenDisabled
import org.team9432.lib.commandbased.input.KTrigger
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.commands.amp.AutoAmp
import org.team9432.robot.commands.amp.ScoreAmp
import org.team9432.robot.commands.drivetrain.FieldOrientedDrive
import org.team9432.robot.commands.intake.Outtake
import org.team9432.robot.commands.intake.TeleIntake
import org.team9432.robot.commands.shooter.Shoot
import org.team9432.robot.commands.shooter.ShootAngle
import org.team9432.robot.subsystems.amp.CommandAmp
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.ChargeUp
import org.team9432.robot.subsystems.led.animations.Chase
import org.team9432.robot.subsystems.led.animations.Confetti
import org.team9432.robot.subsystems.shooter.CommandShooter
import org.team9432.robot.subsystems.vision.Vision
import kotlin.math.truncate

object Controls {
    private val controller = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.075)

    private val slowButton = controller.rightBumper

    val xSpeed get() = -controller.leftY
    val ySpeed get() = -controller.leftX
    val angle get() = -controller.rightX
    val slowDrive get() = slowButton.asBoolean

    private var currentMode = ControllerMode.DEFAULT
        set(value) {
            Logger.recordOutput("ControllerMode", value)
            LEDState.climbMode = value == ControllerMode.CLIMB
            field = value
        }

    private val isDefaultMode = KTrigger { currentMode == ControllerMode.DEFAULT }
    private val isClimbMode = KTrigger { currentMode == ControllerMode.CLIMB }
    private val isLedMode = KTrigger { currentMode == ControllerMode.LED }

    init {
        Drivetrain.defaultCommand = FieldOrientedDrive()

        /* ------------- DEFAULT BUTTONS ------------- */

        // Run Intake
        controller.leftBumper.and(isDefaultMode)
            .whileTrue(TeleIntake().afterSimDelay(2.0) {
                BeambreakIOSim.setNoteInIntakeSide(RobotState.getMovementDirection(), true)
            }) // Pretend to get a note after 2 seconds in sim

        // Outtake Intake
        controller.x.and(isDefaultMode)
            .whileTrue(Outtake())

        // Shoot Speaker
        controller.rightTrigger.and(isDefaultMode)
            .onTrue(Shoot(4000.0, 6000.0))

        // Shoot Amplifier from speaker
        controller.b.and(isDefaultMode)
            .onTrue(ShootAngle(2250.0, 2250.0, Rotation2d.fromDegrees(10.0)))

        // Reset Drivetrain Heading
        controller.a.and(isDefaultMode)
            .onTrue(InstantCommand { Gyro.resetYaw() })

        // Reset
        controller.y.and(isDefaultMode)
            .onTrue(ParallelCommand(
                CommandIntake.stop(),
                CommandHopper.stop(),
                CommandShooter.stop(),
                CommandAmp.stop(),
                InstantCommand {
                    RobotState.notePosition = RobotState.NotePosition.NONE
                    KCommandScheduler.cancelAll()
                }
            ))

        // Load to amp
        controller.leftTrigger.onTrue(ScoreAmp(4.5))

        /* ------------- LED MODE BUTTONS ------------- */

        controller.rightBumper.and(isLedMode)
            .onTrue(InstantCommand { Vision.setLED(true) }.runsWhenDisabled(true))

        controller.leftBumper.and(isLedMode)
            .onTrue(InstantCommand { Vision.setLED(false) }.runsWhenDisabled(true))

        // Toggle chase mode
        controller.a.and(isLedMode)
            .onTrue(InstantCommand {
                if (LEDState.animation == null) LEDState.animation = Chase
                else LEDState.animation = null
            }.runsWhenDisabled(true))

        // Run charge up animation
        controller.y.and(isLedMode)
            .onTrue(InstantCommand {
                LEDState.animation = ChargeUp(1.0, 1.0)
            }.runsWhenDisabled(true))

        // Run confetti
        controller.b.and(isLedMode)
            .onTrue(InstantCommand {
                LEDState.animation = Confetti(6.0)
            }.runsWhenDisabled(true))

        /* -------------- CLIMB BUTTONS -------------- */

        // Raise Left Climber
        controller.leftBumper.and(isClimbMode)
            .onTrue(InstantCommand(LeftClimber) { LeftClimber.setVoltage(6.0) })
            .onFalse(InstantCommand(LeftClimber) { LeftClimber.stop() })

        // Lower Left Climber
        controller.leftTrigger.and(isClimbMode)
            .onTrue(InstantCommand(LeftClimber) { LeftClimber.setVoltage(-6.0) })
            .onFalse(InstantCommand(LeftClimber) { LeftClimber.stop() })

        // Raise Right Climber
        controller.rightBumper.and(isClimbMode)
            .onTrue(InstantCommand(RightClimber) { RightClimber.setVoltage(6.0) })
            .onFalse(InstantCommand(RightClimber) { RightClimber.stop() })

        // Lower Right Climber
        controller.rightTrigger.and(isClimbMode)
            .onTrue(InstantCommand(RightClimber) { RightClimber.setVoltage(-6.0) })
            .onFalse(InstantCommand(RightClimber) { RightClimber.stop() })

        /* -------------- MODE SWITCHING -------------- */

        // Enter LED Mode
        isDefaultMode.and(controller.back)
            .onFalse(InstantCommand { currentMode = ControllerMode.LED }.runsWhenDisabled(true))

        // Enter Climb Mode
        isDefaultMode.and(controller.start)
            .onFalse(InstantCommand { currentMode = ControllerMode.CLIMB }.runsWhenDisabled(true))

        // Reenter Default Mode
        isDefaultMode.negate().and((controller.start).or(controller.back))
            .onFalse(InstantCommand { currentMode = ControllerMode.DEFAULT }.runsWhenDisabled(true))
    }

    private enum class ControllerMode {
        DEFAULT, CLIMB, LED
    }
}
