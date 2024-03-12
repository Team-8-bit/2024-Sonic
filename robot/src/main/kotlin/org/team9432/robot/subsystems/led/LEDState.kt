package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.util.Color
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.led.LEDModes.breath
import org.team9432.robot.subsystems.led.LEDModes.rainbow
import org.team9432.robot.subsystems.led.LEDModes.solid
import org.team9432.robot.subsystems.led.LEDModes.strobe
import org.team9432.robot.subsystems.led.animations.LEDAnimation

object LEDState {
    var noteInIntake = false

    var leftClimberAtLimit = false
    var rightClimberAtLimit = false

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
            if (DriverStation.isDisabled()) {
                breath(LEDColors.MAIN_GREEN, Color.kBlack, LEDs.Section.ALL, 3.0)
            } else if (DriverStation.isAutonomous()) {
                strobe(Color.kRed, 0.25, LEDs.Section.ALL)
            } else { // Teleop
                rainbow(30.0, 0.5, LEDs.Section.ALL) // This will be the default unless overwritten later

                if (noteInIntake) { // Blink purple when there's a note in the intake
                    strobe(Color.kPurple, 0.1, LEDs.Section.TOP + LEDs.Section.TOP_BAR)
                }

                if (leftClimberAtLimit) {
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
    }
}