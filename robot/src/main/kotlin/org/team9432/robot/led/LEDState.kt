package org.team9432.robot.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.util.Color
import org.team9432.Robot
import org.team9432.robot.RobotState
import org.team9432.robot.led.LEDModes.breath
import org.team9432.robot.led.LEDModes.pulse
import org.team9432.robot.led.LEDModes.rainbow
import org.team9432.robot.led.LEDModes.solid
import org.team9432.robot.led.LEDModes.strobe
import org.team9432.robot.led.animations.LEDAnimation
import org.team9432.robot.oi.EmergencySwitches
import org.team9432.robot.sensors.vision.Vision
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber

object LEDState {
    var allianceColor = Color.kWhite

    var noteInIntake = false
    var leftClimberAtLimit = false
    var rightClimberAtLimit = false
    var hasVisionTarget = false
    var limelightConnected = false
    var testEmergencySwitchActive = false

    var animation: LEDAnimation? = null
        set(value) {
            value?.reset()
            field = value
        }

    fun updateBuffer() {
        if (animation != null) {
            animation?.let { animation ->
                val isFinished = animation.updateBuffer()
                if (isFinished) LEDState.animation = null
            }

        } else if (testEmergencySwitchActive) {
            solid(Color.kGreen, LEDs.Section.SPEAKER_LEFT)
            solid(Color.kRed, LEDs.Section.SPEAKER_RIGHT)
            solid(Color.kBlue, LEDs.Section.AMP_LEFT)
            solid(Color.kYellow, LEDs.Section.AMP_RIGHT)
            solid(Color.kWhite, LEDs.Section.TOP_BAR)
        } else {
            solid(Color.kBlack, LEDs.Section.ALL) // Start with everything off

            if (DriverStation.isDisabled()) {
                pulse(Color.kBlack, Color.kWhite, LEDModes.sidePulses, 2.0, 1.0)

                // Set the top to the alliance color, this also shows when the fms is connected
                if (Robot.alliance == null) {
                    breath(Color.kWhite, Color.kBlack, LEDs.Section.TOP_BAR, duration = 2.0)
                } else {
                    solid(allianceColor, LEDs.Section.TOP_BAR)
                }

                if (hasVisionTarget) { // Turn green when the robot can see an apriltag
                    solid(LEDColors.MAIN_GREEN, LEDs.Section.BOTTOM)
                }

                if (!limelightConnected) { // Lime and red when the limelight isn't connected
                    breath(Color.kLime, Color.kRed, LEDs.Section.ALL_BUT_TOP, duration = 0.25)
                }
            } else if (DriverStation.isAutonomous()) {
                strobe(Color.kRed, 0.25, LEDs.Section.ALL)
            } else { // Teleop
                rainbow(30.0, 0.5, LEDs.Section.ALL) // This will be the default unless overwritten later

                if (noteInIntake) { // Blink purple when there's a note in the intake
                    strobe(Color.kPurple, 0.1, LEDs.Section.TOP + LEDs.Section.TOP_BAR)
                }

                if (leftClimberAtLimit) { // When a climber is running into the limit, set that side to red
                    solid(Color.kRed, LEDs.Section.LEFT)
                }
                if (rightClimberAtLimit) {
                    solid(Color.kRed, LEDs.Section.RIGHT)
                }
            }
        }
    }

    fun updateState() {
        noteInIntake = RobotState.notePosition.isIntake
        leftClimberAtLimit = LeftClimber.atLimit && LeftClimber.hasVoltageApplied
        rightClimberAtLimit = RightClimber.atLimit && RightClimber.hasVoltageApplied

        hasVisionTarget = Vision.hasVisionTarget()
        limelightConnected = Vision.connected

        testEmergencySwitchActive = EmergencySwitches.testSwitchActive

        allianceColor = when (Robot.alliance) {
            DriverStation.Alliance.Red -> Color.kRed
            DriverStation.Alliance.Blue -> Color.kBlue
            null -> Color.kWhite
        }
    }
}