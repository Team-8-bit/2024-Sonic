package org.team9432.robot

import edu.wpi.first.wpilibj.GenericHID

object EmergencySwitches {
    private val hid = GenericHID(2)
    val autoAimDisabled get() = hid.getRawButton(6)
    val isSubwooferOnly get() = hid.getRawButton(7)
    val isAmpForSpeaker get() = hid.getRawButton(0)
    val testSwitchActive get() = hid.getRawButton(1)
}