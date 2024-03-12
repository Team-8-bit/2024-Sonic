package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.util.Color
import org.team9432.Robot
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.led.LEDModes.breath
import org.team9432.robot.subsystems.led.LEDModes.rainbow
import org.team9432.robot.subsystems.led.LEDModes.solid
import org.team9432.robot.subsystems.led.LEDModes.strobe
import org.team9432.robot.subsystems.led.animations.LEDAnimation
import org.team9432.robot.subsystems.vision.Vision

object LEDState {
    var allianceColor = Color.kBlack

    var noteInIntake = false

    var leftClimberAtLimit = false
    var rightClimberAtLimit = false

    var hasVisionTarget = false

    var animation: LEDAnimation? = null
        set(value) {
            value?.reset()
            field = value
        }

    fun updateBuffer() {
        if (animation != null) {
            animation?.let { animation ->
                val isFinished = animation.updateBuffer()
                if (isFinished) this.animation = null
            }
        } else {
            solid(Color.kBlack, LEDs.Section.ALL) // Start with everything off

            if (DriverStation.isDisabled()) {
                // Set the top to the alliance color, this also shows when the fms is connected
                solid(allianceColor, LEDs.Section.TOP)

                if (hasVisionTarget) { // Turn silver when the robot can see an apriltag
                    solid(Color.kSilver, LEDs.Section.BOTTOM)
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

        allianceColor = when (Robot.alliance) {
            DriverStation.Alliance.Red -> Color.kFirstRed
            DriverStation.Alliance.Blue -> Color.kFirstBlue
            null -> Color.kWhite
        }
    }
}