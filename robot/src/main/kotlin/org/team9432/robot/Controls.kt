package org.team9432.robot


import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.GenericHID
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.lib.commandbased.commands.runsWhenDisabled
import org.team9432.lib.commandbased.input.KTrigger
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.commands.amp.ScoreAmp
import org.team9432.robot.commands.drivetrain.teleop.TeleDrive
import org.team9432.robot.commands.hood.HoodAimAtSpeaker
import org.team9432.robot.commands.intake.Outtake
import org.team9432.robot.commands.intake.TeleIntake
import org.team9432.robot.commands.shooter.SubwooferShoot
import org.team9432.robot.commands.shooter.TeleShoot
import org.team9432.robot.commands.stopCommand
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.climber.CommandClimber
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro
import org.team9432.robot.subsystems.hood.CommandHood
import org.team9432.robot.subsystems.hood.Hood
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.ChargeUp
import org.team9432.robot.subsystems.led.animations.Confetti
import org.team9432.robot.subsystems.led.animations.Rocket
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
        Drivetrain.defaultCommand = TeleDrive()
        // Hood.defaultCommand = HoodAimAtSpeaker()

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
            .onTrue(SuppliedCommand {
                if (EmergencySwitches.isAmpForSpeaker) ScoreAmp(12.0)
                else if (EmergencySwitches.isSubwooferOnly) SubwooferShoot()
                else TeleShoot()
            })

        // Shoot Amplifier from speaker
        driver.b.and(isDefaultMode)
            .onTrue(InstantCommand { Gyro.setYaw(Rotation2d(Math.PI)) }.runsWhenDisabled(true))

        // Reset Drivetrain Heading
        driver.a.and(isDefaultMode)
            .onTrue(InstantCommand { Gyro.resetYaw() }.runsWhenDisabled(true))

        // Reset
        driver.y.and(isDefaultMode)
            .onTrue(stopCommand())

        // Load to amp
        driver.leftTrigger.and(isDefaultMode)
            .onTrue(ScoreAmp(4.5))

        /* ------------- LED MODE BUTTONS ------------- */

        driver.rightBumper.and(isLedMode)
            .onTrue(InstantCommand { Vision.setLED(true) }.runsWhenDisabled(true))

        driver.leftBumper.and(isLedMode)
            .onTrue(InstantCommand { Vision.setLED(false) }.runsWhenDisabled(true))

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

       // operator.x.whileTrue(CommandHood.followAngle({ Rotation2d.fromDegrees(30.0) }))
        //operator.x.whileTrue(CommandHood.followAngle({ Rotation2d.fromDegrees(0.0) }))

        /* -------------- MODE SWITCHING -------------- */

        // Enter LED Mode
        isDefaultMode.and(driver.back)
            .onFalse(InstantCommand { currentMode = ControllerMode.LED }.runsWhenDisabled(true))

        // Enter Default Mode
        isDefaultMode.negate().and((driver.start).or(driver.back))
            .onFalse(InstantCommand { currentMode = ControllerMode.DEFAULT }.runsWhenDisabled(true))
    }

    fun setDriverRumble(magnitude: Double) {
        driver.setRumble(GenericHID.RumbleType.kBothRumble, magnitude)
    }

    private enum class ControllerMode {
        DEFAULT, LED
    }
}
