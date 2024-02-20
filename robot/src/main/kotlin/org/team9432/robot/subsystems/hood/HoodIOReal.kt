package org.team9432.robot.subsystems.hood

import com.revrobotics.CANSparkBase
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.Ports

class HoodIOReal: HoodIO, SubsystemBase() {
    private val spark = KSparkMAX(Ports.Hood.MOTOR_ID) {
        inverted = false
        idleMode = CANSparkBase.IdleMode.kBrake
    }

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