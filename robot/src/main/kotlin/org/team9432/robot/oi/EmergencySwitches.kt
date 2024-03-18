package org.team9432.robot.oi

import edu.wpi.first.wpilibj.GenericHID

object EmergencySwitches {
    private val hid = GenericHID(2)
    val autoAimDisabled get() = hid.getRawButton(7)
    val isSubwooferOnly get() = hid.getRawButton(8)
    val isAmpForSpeaker get() = hid.getRawButton(1)
    val testSwitchActive get() = hid.getRawButton(2)
}