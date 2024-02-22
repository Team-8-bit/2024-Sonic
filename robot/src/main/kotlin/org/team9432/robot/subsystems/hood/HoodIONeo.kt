package org.team9432.robot.subsystems.hood

import com.revrobotics.CANSparkBase
import edu.wpi.first.wpilibj.DigitalInput
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.Ports

class HoodIONeo: HoodIO {
    private val spark = KSparkMAX(Ports.Hood.MOTOR_ID) {
        inverted = false
        idleMode = CANSparkBase.IdleMode.kBrake
        openLoopRampRate = 0.5

        setPIDConstants(p = 0.0)
    }

    private val encoder = spark.absoluteEncoder
    private val controller = spark.pidController
    private val limit = DigitalInput(Ports.Hood.LIMIT_ID)
    private var targetAngle = 0.0
    private val GEAR_RATIO = 1

    init {
        controller.setFeedbackDevice(encoder)
    }

    override fun setAngle(angle: Double) {
        targetAngle = angle * GEAR_RATIO
        controller.setReference(targetAngle, CANSparkBase.ControlType.kPosition)
    }

    override fun updateInputs(inputs: HoodIO.HoodIOInputs) {
        inputs.absolutePosition = encoder.position
        inputs.atLimit = limit.get()
    }
}