package org.team9432.robot.subsystems.hopper

import edu.wpi.first.wpilibj.DigitalInput
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.Devices

class HopperIOReal: HopperIO {
    private val spark = KSparkMAX(Devices.HOPPER_ID)
    private val ampBeamBreak = DigitalInput(Devices.HOPPER_AMP_SIDE_BEAMBREAK_PORT)
    private val shooterBeamBreak = DigitalInput(Devices.HOPPER_SHOOTER_SIDE_BEAMBREAK_PORT)

    override fun updateInputs(inputs: HopperIO.HopperIOInputs) {
        inputs.atAmpBeamBreak = ampBeamBreak.get()
        inputs.atShooterBeamBreak = shooterBeamBreak.get()
    }

    override fun setVoltage(volts: Double) {
        spark.setVoltage(volts)
    }

    override fun stop() {
        spark.setVoltage(0.0)
    }
}