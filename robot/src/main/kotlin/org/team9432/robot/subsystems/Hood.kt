package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.littletonrobotics.junction.Logger
import org.team9432.lib.KSysIdConfig
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.SysIdUtil.getSysIdTests
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.logged.neo.LoggedNeo
import org.team9432.lib.unit.asRotation2d
import org.team9432.lib.unit.degrees
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.RobotPosition
import org.team9432.robot.oi.switches.DSSwitches

object Hood: KSubsystem() {
    private val motor = LoggedNeo(getConfig())

    private val distanceAngleMap = InterpolatingDoubleTreeMap()

    private val feedforward: ArmFeedforward
    private val pid = ProfiledPIDController(0.0, 0.0, 0.0, TrapezoidProfile.Constraints(0.0, 0.0))

    // The offset of the arm from the horizontal in its neutral position, measured from horizontal
    private val hoodOffset = Rotation2d()

    init {
        when (State.mode) {
            REAL, REPLAY -> {
                pid.setPID(5.0, 0.0, 0.0)
                pid.constraints = TrapezoidProfile.Constraints(Math.toRadians(180.0), Math.toRadians(540.0))
                feedforward = ArmFeedforward(0.0, 0.21, 0.0, 0.0)
            }

            SIM -> {
                pid.setPID(1.0, 0.0, 0.0)
                feedforward = ArmFeedforward(0.0, 0.0, 0.0, 0.0)
            }
        }


        distanceAngleMap.put(1.0, 3.0)
        distanceAngleMap.put(1.5, 15.0)
        distanceAngleMap.put(2.0, 18.0)
        distanceAngleMap.put(2.5, 22.7)
        distanceAngleMap.put(3.0, 26.0)
        distanceAngleMap.put(3.5, 30.0)

        setAngle(Rotation2d())

        pid.setTolerance(Math.toRadians(1.0))

//        SmartDashboard.putNumber("tableValue", 0.0)
    }

    override fun periodic() {
        val inputs = motor.updateAndRecordInputs()

//        val tableValue = SmartDashboard.getNumber("tableValue", 0.0)
        //distanceAngleMap.put(3.0, tableValue)

        Logger.recordOutput("Subsystems/Hood", Pose3d(Translation3d(0.266700, 0.0, 0.209550 + 0.124460), Rotation3d(0.0, inputs.angle.radians, 0.0)))

        Logger.recordOutput("Hood/AngleSetpointDegrees", Math.toDegrees(pid.setpoint.position) - hoodOffset.degrees)
        Logger.recordOutput("Hood/AngleDegrees", inputs.angle.degrees)

        if (DSSwitches.hoodDisabled) {
            motor.stop()
            return
        }

        val feedback = pid.calculate((inputs.angle + hoodOffset).radians)
        val feedforward = feedforward.calculate(pid.setpoint.position, pid.setpoint.velocity)

        motor.setVoltage(feedforward + feedback)
    }

    fun setAngle(angle: Rotation2d) {
        if (DSSwitches.hoodDisabled) return

        val angleTarget = Rotation2d.fromDegrees(MathUtil.clamp(angle.degrees, 0.0, 30.0)) + hoodOffset
        val goal = TrapezoidProfile.State(angleTarget.radians, 0.0)
        pid.goal = goal
    }

    fun getAngleToSpeaker(): Rotation2d {
        val angle = Rotation2d.fromDegrees(distanceAngleMap.get(RobotPosition.distanceToSpeaker()))
        Logger.recordOutput("Hood/SpeakerAngleTarget", angle.degrees)
        return angle
    }

    fun setVoltage(volts: Double) {
        if (DSSwitches.hoodDisabled) return
        motor.setVoltage(volts)
    }

    fun setSysIdVoltage(volts: Double) {
        motor.setVoltage(volts)
    }

    fun resetAngle() = motor.resetEncoder()
    fun stop() = motor.stop()

    object Commands {
        fun stop() = InstantCommand(Hood) { Hood.stop() }
        fun followAngle(angle: () -> Rotation2d) = SimpleCommand(
            requirements = setOf(Hood),
            execute = { setAngle(angle.invoke()) },
            isFinished = { DSSwitches.hoodDisabled },
            end = { setAngle(0.0.degrees.asRotation2d) }
        )

        fun aimAtSpeaker() = followAngle(::getAngleToSpeaker)
        fun setVoltage(volts: Double) = InstantCommand(Hood) { Hood.setVoltage(volts) }
    }

    private fun getConfig() = LoggedNeo.Config(
        canID = Devices.HOOD_ID,
        motorType = Spark.MotorType.NEO,
        deviceName = "Hood Motor",
        logName = "Hood",
        gearRatio = 2.0 * (150 / 15), // 20
        simJkgMetersSquared = 0.01507,
        sparkConfig = Spark.Config(
            inverted = true,
            idleMode = CANSparkBase.IdleMode.kBrake,
            stallCurrentLimit = 20
        )
    )

    fun getSysIdTests() = motor.getSysIdTests(KSysIdConfig())
}