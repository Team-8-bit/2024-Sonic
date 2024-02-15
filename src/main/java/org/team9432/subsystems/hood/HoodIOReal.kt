package org.team9432.subsystems.hood

import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team9432.Ports

class HoodIOReal: HoodIO, SubsystemBase() {
    private val spark = CANSparkMax(Ports.Hood.MOTOR_ID, CANSparkLowLevel.MotorType.kBrushless)
    private val encoder = spark.absoluteEncoder
    private val limit = DigitalInput(Ports.Hood.LIMIT_ID)

    private val controller = PIDController(0.0, 0.0, 0.0)
    private var targetAngle = 0.0

    private val GEAR_RATIO = 1

    override fun periodic() {
        val speed = controller.calculate(encoder.position, targetAngle)
        spark.set(speed)
    }

    override fun setAngle(angle: Double) {
        targetAngle = angle * GEAR_RATIO
    }

    override fun updateInputs(inputs: HoodIO.HoodIOInputs) {
        inputs.absolutePosition = encoder.position
        inputs.atLimit = limit.get()
    }
}