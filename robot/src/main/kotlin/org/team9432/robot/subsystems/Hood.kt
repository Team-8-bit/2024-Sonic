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
import org.team9432.lib.logged.neo.LoggedNeo
import org.team9432.lib.unit.asRotation2d
import org.team9432.lib.unit.degrees
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.RobotPosition
import org.team9432.robot.oi.EmergencySwitches

object Hood: KSubsystem() {
    private val motor = LoggedNeo(getConfig())

    private val ffTable = InterpolatingDoubleTreeMap()
    private val distanceAngleMap = InterpolatingDoubleTreeMap()

    init {
        when (State.mode) {
            REAL, REPLAY -> motor.setPID(2.25, 0.0, 0.0)
            SIM -> motor.setPID(1.0, 0.0, 0.0)
        }

        distanceAngleMap.put(1.0, 0.0)
        distanceAngleMap.put(1.8, 15.0)
        distanceAngleMap.put(2.8, 22.0)

        ffTable.put(0.0, 0.0)
        ffTable.put(15.0, 10.0)
        ffTable.put(30.0, 0.0)
    }

    override fun periodic() {
        val inputs = motor.updateAndRecordInputs()
        Logger.recordOutput("Subsystems/Hood", Pose3d(Translation3d(0.266700, 0.0, 0.209550 + 0.124460), Rotation3d(0.0, inputs.angle.radians, 0.0)))
        if (EmergencySwitches.disableHood) motor.stop()
    }

    fun setAngle(angle: Rotation2d) {
        if (EmergencySwitches.disableHood) return
        motor.setAngle(Rotation2d.fromDegrees(MathUtil.clamp(angle.degrees, 0.0, 30.0)))

        Logger.recordOutput("Hood/AngleSetpointDegrees", angle.degrees)
    }

    fun getAngleToSpeaker(): Rotation2d {
        val angle = Rotation2d.fromDegrees(distanceAngleMap.get(RobotPosition.distanceToSpeaker()))
        Logger.recordOutput("Hood/SpeakerAngleTarget", angle.degrees)
        return angle
    }

    fun setVoltage(volts: Double) {
        if (EmergencySwitches.disableHood) return
        motor.setVoltage(volts)
    }

    fun resetAngle() = motor.resetEncoder()
    fun stop() = motor.stop()

    object Commands {
        fun stop() = InstantCommand(Hood) { Hood.stop() }
        fun followAngle(angle: () -> Rotation2d) = SimpleCommand(
            requirements = setOf(Hood),
            execute = { setAngle(angle.invoke()) },
            isFinished = { EmergencySwitches.disableHood },
            end = { setAngle(0.0.degrees.asRotation2d) }
        )

        fun aimAtSpeaker() = followAngle(::getAngleToSpeaker)

        fun setVoltage(volts: Double) = InstantCommand(Hood) { Hood.setVoltage(volts) }
        fun resetAngle() = InstantCommand(Hood) { Hood.resetAngle() }
    }

    private fun getConfig() = LoggedNeo.Config(
        canID = Devices.HOOD_ID,
        motorType = Spark.MotorType.NEO,
        deviceName = "Hood Motor",
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