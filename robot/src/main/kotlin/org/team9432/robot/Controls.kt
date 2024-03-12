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
import org.team9432.robot.commands.amp.ScoreAmp
import org.team9432.robot.commands.drivetrain.FieldOrientedDrive
import org.team9432.robot.commands.intake.Outtake
import org.team9432.robot.commands.intake.TeleIntake
import org.team9432.robot.commands.shooter.TeleShoot
import org.team9432.robot.commands.shooter.ShootAngle
import org.team9432.robot.subsystems.amp.CommandAmp
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.climber.CommandClimber
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro
import org.team9432.robot.subsystems.hood.CommandHood
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.ChargeUp
import org.team9432.robot.subsystems.led.animations.Chase
import org.team9432.robot.subsystems.led.animations.Confetti
import org.team9432.robot.subsystems.led.animations.Rocket
import org.team9432.robot.subsystems.shooter.CommandShooter
import org.team9432.robot.subsystems.vision.Vision

object Controls {
    private val driver = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.075)
    private val operator = KXboxController(1)

    private val slowButton = driver.rightBumper

    val xSpeed get() = -driver.leftY
    val ySpeed get() = -driver.leftX
    val angle get() = -driver.rightX
    val slowDrive get() = slowButton.asBoolean

    private var currentMode = ControllerMode.DEFAULT
        set(value) {
            Logger.recordOutput("ControllerMode", value)
            field = value
        }

    private val isDefaultMode = KTrigger { currentMode == ControllerMode.DEFAULT }
    private val isLedMode = KTrigger { currentMode == ControllerMode.LED }

    init {
        Drivetrain.defaultCommand = FieldOrientedDrive()

        /* ------------- DEFAULT BUTTONS ------------- */

        // Run Intake
        driver.leftBumper.and(isDefaultMode)
            .whileTrue(TeleIntake().afterSimDelay(2.0) {
                BeambreakIOSim.setNoteInIntakeSide(RobotState.getMovementDirection(), true)
            }) // Pretend to get a note after 2 seconds in sim

        // Outtake Intake
        driver.x.and(isDefaultMode)
            .whileTrue(Outtake())

        // Shoot Speaker
        driver.rightTrigger.and(isDefaultMode)
            .onTrue(TeleShoot())

        // Shoot Amplifier from speaker
        driver.b.and(isDefaultMode)
            .onTrue(ShootAngle(2250.0, 2250.0, Rotation2d.fromDegrees(10.0)))

        // Reset Drivetrain Heading
        driver.a.and(isDefaultMode)
            .onTrue(InstantCommand { Gyro.resetYaw() })

        // Reset
        driver.y.and(isDefaultMode)
            .onTrue(
                ParallelCommand(
                    InstantCommand {
                        RobotState.notePosition = RobotState.NotePosition.NONE
                        KCommandScheduler.cancelAll()
                    },
                    CommandIntake.stop(),
                    CommandHopper.stop(),
                    CommandShooter.stop(),
                    CommandAmp.stop(),
                    CommandClimber.stop(),
                    CommandHood.stop()
                )
            )

        // Load to amp
        driver.leftTrigger.and(isDefaultMode)
            .onTrue(ScoreAmp(4.5))

        /* ------------- LED MODE BUTTONS ------------- */

        driver.rightBumper.and(isLedMode)
            .onTrue(InstantCommand { Vision.setLED(true) }.runsWhenDisabled(true))

        driver.leftBumper.and(isLedMode)
            .onTrue(InstantCommand { Vision.setLED(false) }.runsWhenDisabled(true))

        // Toggle chase mode
        driver.a.and(isLedMode)
            .onTrue(InstantCommand {
                if (LEDState.animation == null) LEDState.animation = Chase
                else LEDState.animation = null
            }.runsWhenDisabled(true))

        // Run charge up animation
        driver.y.and(isLedMode)
            .onTrue(InstantCommand {
                LEDState.animation = ChargeUp(1.0, 1.0)
            }.runsWhenDisabled(true))
            .onFalse(InstantCommand {
                LEDState.animation = Rocket(0.5)
            }.runsWhenDisabled(true))

        // Run confetti
        driver.b.and(isLedMode)
            .onTrue(InstantCommand {
                LEDState.animation = Confetti(6.0)
            }.runsWhenDisabled(true))

        /* -------------- CLIMB BUTTONS -------------- */

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

        /* -------------- MODE SWITCHING -------------- */

        // Enter LED Mode
        isDefaultMode.and(driver.back)
            .onFalse(InstantCommand { currentMode = ControllerMode.LED }.runsWhenDisabled(true))

        // Enter Default Mode
        isDefaultMode.negate().and((driver.start).or(driver.back))
            .onFalse(InstantCommand { currentMode = ControllerMode.DEFAULT }.runsWhenDisabled(true))
    }

    private enum class ControllerMode {
        DEFAULT, LED
    }
}
