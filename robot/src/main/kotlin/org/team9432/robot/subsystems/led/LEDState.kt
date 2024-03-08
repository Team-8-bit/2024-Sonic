package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.util.Color
import org.team9432.robot.subsystems.led.LEDModes.breath
import org.team9432.robot.subsystems.led.LEDModes.rainbow
import org.team9432.robot.subsystems.led.LEDModes.strobe

object LEDState {
    var isIntakeLightOn = false

    fun updateBuffer() {
        if (DriverStation.isDisabled()) {
            breath(LEDColors.MAIN_GREEN, Color.kBlack, LEDs.Strip.ALL, 3.0)

        } else if (DriverStation.isAutonomous()) {
            strobe(Color.kRed, 0.25, LEDs.Strip.ALL)

        } else { // Teleop
            rainbow(30.0, 0.5, LEDs.Strip.ALL) // This will be the default unless overwritten later

            if (isIntakeLightOn) { // Blink purple while intaking
                strobe(Color.kPurple, 0.1, LEDs.Strip.BOTTOM)
            }
        }
    }
}