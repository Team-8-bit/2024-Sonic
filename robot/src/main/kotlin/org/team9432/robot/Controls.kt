package org.team9432.robot


import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.lib.commandbased.input.KTrigger
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.commands.drivetrain.FieldOrientedDrive
import org.team9432.robot.commands.intake.Outtake
import org.team9432.robot.commands.intake.TeleIntake
import org.team9432.robot.commands.shooter.ShootStatic
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.Chase
import org.team9432.robot.subsystems.shooter.CommandShooter
import org.team9432.robot.subsystems.vision.Vision

object Controls {
    private val controller = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.075)

    val xSpeed get() = -controller.leftY
    val ySpeed get() = -controller.leftX
    val angle get() = -controller.rightX
    val slowDrive get() = controller.rightBumper.asBoolean

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
            .onTrue(ShootStatic(4000.0, 6000.0))

        // Shoot Amplifier
        controller.leftTrigger.and(isDefaultMode)
            .onTrue(ShootStatic(2250.0, 2250.0))

        // Reset Drivetrain Heading
        controller.a.and(isDefaultMode)
            .onTrue(InstantCommand { Gyro.resetYaw() })

        // Reset
        controller.b.and(isDefaultMode)
            .onTrue(ParallelCommand(
                CommandIntake.stop(),
                CommandHopper.stop(),
                CommandShooter.stop(),
                InstantCommand {
                    RobotState.notePosition = RobotState.NotePosition.NONE
                    KCommandScheduler.cancelAll()
                }
            ))

        // Clear note position
        controller.y.onTrue(InstantCommand {
            BeambreakIOSim.setNoteInIntakeAmpSide(false)
            BeambreakIOSim.setNoteInIntakeSpeakerSide(false)
            BeambreakIOSim.setNoteInHopperAmpSide(false)
            BeambreakIOSim.setNoteInHopperSpeakerSide(false)
            BeambreakIOSim.setNoteInCenter(false)
            RobotState.notePosition = RobotState.NotePosition.NONE
        })

        /* ------------- LED MODE BUTTONS ------------- */

        controller.rightBumper.and(isLedMode)
            .onTrue(InstantCommand { Vision.setLED(true) })

        controller.leftBumper.and(isLedMode)
            .onTrue(InstantCommand { Vision.setLED(false) })

        // Toggle chase mode
        controller.a.and(isLedMode)
            .onTrue(InstantCommand {
                if (LEDState.animation == null) LEDState.animation = Chase
                else LEDState.animation = null
            })

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
            .onFalse(InstantCommand { currentMode = ControllerMode.LED })

        // Enter Climb Mode
        isDefaultMode.and(controller.start)
            .onFalse(InstantCommand { currentMode = ControllerMode.CLIMB })

        // Reenter Default Mode
        isDefaultMode.negate().and((controller.start).or(controller.back))
            .onFalse(InstantCommand { currentMode = ControllerMode.DEFAULT })
    }

    private enum class ControllerMode {
        DEFAULT, CLIMB, LED
    }
}
