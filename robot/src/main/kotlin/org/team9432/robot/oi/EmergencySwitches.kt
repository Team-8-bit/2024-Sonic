package org.team9432.robot.oi

import edu.wpi.first.wpilibj.GenericHID

object EmergencySwitches {
    private val switches = GenericHID(2)
    val disableHood get() = switches.getRawButton(8)
    val useAmpForSpeaker get() = switches.getRawButton(1)
    val testSwitchActive get() = switches.getRawButton(2)
    val disableDrivetrain get() = switches.getRawButton(4)
}