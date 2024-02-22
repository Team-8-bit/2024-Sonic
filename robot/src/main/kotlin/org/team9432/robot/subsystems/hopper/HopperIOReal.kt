package org.team9432.robot.subsystems.hopper

import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team9432.robot.Ports

class HopperIOReal: HopperIO, SubsystemBase() {
    private val spark = CANSparkMax(Ports.Hopper.MOTOR_ID, CANSparkLowLevel.MotorType.kBrushless)
    private val ampBeamBreak = DigitalInput(Ports.Hopper.AMP_BREAK_ID)
    private val shooterBeamBreak = DigitalInput(Ports.Hopper.SHOOTER_BREAK_ID)
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