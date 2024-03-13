package org.team9432.robot

import edu.wpi.first.wpilibj.GenericHID

object EmergencySwitches {
    private val hid = GenericHID(2)
    val autoAimDisabled get() = hid.getRawButton(0)
    val isSubwooferOnly get() = hid.getRawButton(1)
    val isAmpForSpeaker get() = hid.getRawButton(2)
}