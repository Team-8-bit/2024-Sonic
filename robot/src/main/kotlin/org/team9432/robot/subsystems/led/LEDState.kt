package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.util.Color
import org.team9432.robot.subsystems.led.LEDModes.breath
import org.team9432.robot.subsystems.led.LEDModes.rainbow
import org.team9432.robot.subsystems.led.LEDModes.strobe
import org.team9432.robot.subsystems.led.animations.LEDAnimation

object LEDState {
    var intakeLightOn = false
    var climbMode = false

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

                if (intakeLightOn) { // Blink purple while intaking
                    strobe(Color.kPurple, 0.1, LEDs.Section.BOTTOM + LEDs.Section.TOP_BAR)
                }

                if (climbMode) {
                    strobe(Color.kGold, 1.0, LEDs.Section.ALL_BUT_TOP)
                }
            }
        }
    }
}