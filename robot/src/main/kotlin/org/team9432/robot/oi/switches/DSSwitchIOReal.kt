package org.team9432.robot.oi.switches

import edu.wpi.first.wpilibj.GenericHID

object DSSwitchIOReal: DSSwitchIO {
    private val hid = GenericHID(2)

    override fun updateInputs(inputs: DSSwitchIO.DSSwitchIOInputs) {
        inputs.connected = hid.isConnected

        inputs.disableHood = hid.getRawButton(7)
        inputs.disableDrivetrain = hid.getRawButton(8)
        inputs.useAmpForSpeaker = hid.getRawButton(1)
        inputs.testSwitch = hid.getRawButton(2)
        inputs.primaryMechanismAmp = hid.getRawButton(4)
    }
}