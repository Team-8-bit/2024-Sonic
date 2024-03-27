package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap
import org.littletonrobotics.junction.Logger
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.motors.neo.Neo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.oi.EmergencySwitches

object Hood: KSubsystem() {
    private val motor = Neo(getConfig())

    private val ffTable = InterpolatingDoubleTreeMap()

    init {
        when (State.mode) {
            REAL, REPLAY -> motor.setPID(2.25, 0.0, 0.0)
            SIM -> motor.setPID(1.0, 0.0, 0.0)
        }

        ffTable.put(0.0, 0.0)
        ffTable.put(15.0, 10.0)
        ffTable.put(30.0, 0.0)
    }

    override fun periodic() {
        Logger.recordOutput("Subsystems/Hood", Pose3d(Translation3d(0.266700, 0.0, 0.209550 + 0.124460), Rotation3d(0.0, motor.inputs.angle.radians, 0.0)))

        if (EmergencySwitches.isSubwooferOnly) {
            motor.stop()
        }
    }

    fun setAngle(angle: Rotation2d) {
        if (!EmergencySwitches.isSubwooferOnly) {
            motor.setAngle(Rotation2d.fromDegrees(MathUtil.clamp(angle.degrees, 0.0, 30.0)))
        }

        Logger.recordOutput("Hood/AngleSetpointDegrees", angle.degrees)
    }

    fun setVoltage(volts: Double) {
        if (!EmergencySwitches.isSubwooferOnly) {
            motor.setVoltage(volts)
        }
    }

    fun resetAngle() {
        motor.resetEncoder()
    }

    fun stop() = motor.stop()

    object Commands {
        fun stop() = InstantCommand(Hood) { Hood.stop() }
        fun followAngle(angle: () -> Rotation2d) = SimpleCommand(
            requirements = setOf(Hood),
            execute = { setAngle(angle.invoke()) },
            end = { setAngle(Rotation2d()) }
        )

        fun setVoltage(volts: Double) = InstantCommand(Hood) { Hood.setVoltage(volts) }
        fun resetAngle() = InstantCommand(Hood) { Hood.resetAngle() }
    }

    private fun getConfig() = Neo.Config(
        canID = Devices.HOOD_ID,
        motorType = Spark.MotorType.NEO,
        name = "Hood Motor",
        logName = "Hood",
        gearRatio = 2.0 * (150 / 15),
        simJkgMetersSquared = 0.01507,
        feedForwardSupplier = { setpoint -> ffTable.get(setpoint) },
        sparkConfig = Spark.Config(
            inverted = true,
            idleMode = CANSparkBase.IdleMode.kBrake,
            smartCurrentLimit = 20
        )
    )
}