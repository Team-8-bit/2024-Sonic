package org.team9432.subsystems.amp

import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team9432.Ports
import org.team9432.subsystems.hood.HoodIO

class AmpIOReal: AmpIO, SubsystemBase() {
    private val spark = CANSparkMax(Ports.Amp.MOTOR_ID, CANSparkLowLevel.MotorType.kBrushless)
    private var speed = 0.0
    override fun updateInputs(inputs: AmpIO.AmpIOInputs) {}


    override fun setSpeed(speed: Double){
      this.speed = speed
    }

    override fun periodic() {
        spark.set(speed)
    }
}
