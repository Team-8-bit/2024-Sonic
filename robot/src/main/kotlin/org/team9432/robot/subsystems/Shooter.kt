package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.SysIdUtil.getSysIdTests
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.logged.neo.LoggedNeo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.RobotPosition


object Shooter: KSubsystem() {
    private val leftSide = LoggedNeo(getConfig(Devices.LEFT_SHOOTER_ID, false, "Left"))
    private val rightSide = LoggedNeo(getConfig(Devices.RIGHT_SHOOTER_ID, true, "Right"))

    private var isRunningAtSpeeds: Pair<Double, Double>? = null

    private val feedforward: SimpleMotorFeedforward

    private val leftPID = PIDController(0.0, 0.0, 0.0)
    private val rightPID = PIDController(0.0, 0.0, 0.0)

    init {
        when (State.mode) {
            REAL, REPLAY -> {
                leftPID.setPID(0.0039231, 0.0, 0.0)
                rightPID.setPID(0.0039231, 0.0, 0.0)

//                leftPID.setPID(0.013175, 0.0, 0.0)
//                rightPID.setPID(0.013175, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)
//                feedforward = SimpleMotorFeedforward(0.0, 0.0091634, 0.0038234)
//                feedforward = SimpleMotorFeedforward(0.05611, 0.0091634, 0.0038234)

//                leftSide.setPID(0.0005, 0.0, 0.0)
//                rightSide.setPID(0.0005, 0.0, 0.0)
//                feedforward = SimpleMotorFeedforward(0.0, 0.0, 0.0)
            }

            SIM -> {
                leftSide.setPID(0.1, 0.0, 0.0)
                rightSide.setPID(0.1, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0, 0.0)
            }
        }
    }

    override fun periodic() {
        val leftInputs = leftSide.updateAndRecordInputs()
        val rightInputs = rightSide.updateAndRecordInputs()

        isRunningAtSpeeds?.let {
            val (rpmFast, rpmSlow) = it

            val (leftSpeed, rightSpeed) = when (RobotPosition.getSpeakerSide()) {
                RobotPosition.SpeakerSide.LEFT -> rpmFast to rpmSlow
                RobotPosition.SpeakerSide.RIGHT -> rpmSlow to rpmFast
                RobotPosition.SpeakerSide.CENTER -> rpmSlow to rpmFast
            }

            val leftRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(leftSpeed)
            val rightRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(rightSpeed)
            leftSide.setVoltage(leftPID.calculate(leftInputs.velocityRadPerSec, leftRadPerSec) + feedforward.calculate(leftRadPerSec))
            rightSide.setVoltage(rightPID.calculate(rightInputs.velocityRadPerSec, rightRadPerSec) + feedforward.calculate(rightRadPerSec))

            Logger.recordOutput("Shooter/LeftSide/SetpointRPM", leftSpeed)
            Logger.recordOutput("Shooter/RightSide/SetpointRPM", rightSpeed)

            Logger.recordOutput("Shooter/LeftSide/FFCalc", feedforward.calculate(leftRadPerSec))
            Logger.recordOutput("Shooter/RightSide/FFCalc", feedforward.calculate(rightRadPerSec))
        }

        Logger.recordOutput("Shooter/LeftSide/RPM", Units.radiansPerSecondToRotationsPerMinute(leftInputs.velocityRadPerSec))
        Logger.recordOutput("Shooter/RightSide/RPM", Units.radiansPerSecondToRotationsPerMinute(rightInputs.velocityRadPerSec))
        if (Robot.isTest) {
            Logger.recordOutput("Shooter/LeftSide/PositionRadians", leftInputs.angle.radians)
            Logger.recordOutput("Shooter/RightSide/PositionRadians", rightInputs.angle.radians)
        }
    }

    fun startRunAtSpeeds(rpmFast: Double = 6000.0, rpmSlow: Double = 4000.0) {
        isRunningAtSpeeds = rpmFast to rpmSlow
    }

    fun setVoltage(leftVolts: Double, rightVolts: Double) {
        isRunningAtSpeeds = null
        leftSide.setVoltage(leftVolts)
        rightSide.setVoltage(rightVolts)
    }

    fun stop() {
        isRunningAtSpeeds = null
        leftSide.stop()
        rightSide.stop()
    }

    object Commands {
        fun setVoltage(leftVolts: Double, rightVolts: Double) = InstantCommand(Shooter) { Shooter.setVoltage(leftVolts, rightVolts) }
        fun stop() = InstantCommand(Shooter) { Shooter.stop() }

        fun startRunAtSpeeds(rpmFast: Double = 6000.0, rpmSlow: Double = 4000.0) = InstantCommand(Shooter) { Shooter.startRunAtSpeeds(rpmFast, rpmSlow) }
    }

    private fun getConfig(canID: Int, inverted: Boolean, side: String): LoggedNeo.Config {
        return LoggedNeo.Config(
            canID = canID,
            motorType = Spark.MotorType.VORTEX,
            deviceName = "$side Shooter",
            sparkConfig = Spark.Config(
                inverted = inverted,
                idleMode = CANSparkBase.IdleMode.kCoast,
                smartCurrentLimit = 80
            ),
            feedForwardSupplier = { feedforward.calculate(it) },
            additionalQualifier = side,
            logName = "Shooter",
            gearRatio = 0.5,
            simJkgMetersSquared = 0.003
        )
    }

    fun getSysIdTests() = leftSide.getSysIdTests()
}