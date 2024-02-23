package org.team9432.robot.subsystems.hopper

import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team9432.robot.Devices

class HopperIOReal: HopperIO, SubsystemBase() {
    private val spark = CANSparkMax(Devices.HOPPER_ID, CANSparkLowLevel.MotorType.kBrushless)
    private val ampBeamBreak = DigitalInput(Devices.HOPPER_AMP_SIDE_BEAMBREAK_PORT)
    private val shooterBeamBreak = DigitalInput(Devices.HOPPER_SHOOTER_SIDE_BEAMBREAK_PORT)
    private var speed = 0.0

    override fun periodic() {
        spark.set(speed)
    }

    override fun updateInputs(inputs: HopperIO.HopperIOInputs) {
        inputs.atAmpBeamBreak = ampBeamBreak.get()
        inputs.atShooterBeamBreak = shooterBeamBreak.get()
    }

    override fun setSpeed(speed: Double) {
        this.speed = speed
    }
}