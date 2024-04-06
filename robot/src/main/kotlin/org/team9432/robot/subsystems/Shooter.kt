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
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.logged.neo.LoggedNeo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.RobotPosition


object Shooter: KSubsystem() {
    private val leftSide = LoggedNeo(getConfig(Devices.LEFT_SHOOTER_ID, false, "Left"))
    private val rightSide = LoggedNeo(getConfig(Devices.RIGHT_SHOOTER_ID, true, "Right"))

    private val feedforward: SimpleMotorFeedforward

    private val leftPID = PIDController(0.0, 0.0, 0.0)
    private val rightPID = PIDController(0.0, 0.0, 0.0)

    init {
        when (State.mode) {
            REAL, REPLAY -> {
                leftPID.setPID(0.0039231, 0.0, 0.0)
                rightPID.setPID(0.0039231, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)
            }

            SIM -> {
                feedforward = SimpleMotorFeedforward(0.0, 0.0, 0.0)
            }
        }
    }

    override fun periodic() {
        val leftInputs = leftSide.updateAndRecordInputs()
        val rightInputs = rightSide.updateAndRecordInputs()

        val feedforwardVolts = feedforward.calculate(leftPID.setpoint)
        leftSide.setVoltage(leftPID.calculate(leftInputs.velocityRadPerSec) + feedforwardVolts)
        rightSide.setVoltage(rightPID.calculate(rightInputs.velocityRadPerSec) + feedforward.calculate(rightPID.setpoint))

        Logger.recordOutput("Shooter/FFCalc", feedforwardVolts)

        Logger.recordOutput("Shooter/LeftSide/RPM", Units.radiansPerSecondToRotationsPerMinute(leftInputs.velocityRadPerSec))
        Logger.recordOutput("Shooter/RightSide/RPM", Units.radiansPerSecondToRotationsPerMinute(rightInputs.velocityRadPerSec))
        if (Robot.isTest) {
            Logger.recordOutput("Shooter/LeftSide/PositionRadians", leftInputs.angle.radians)
            Logger.recordOutput("Shooter/RightSide/PositionRadians", rightInputs.angle.radians)
        }
    }

    fun setSpeeds(leftRPM: Double, rightRPM: Double) {
        Logger.recordOutput("Shooter/LeftSide/SetpointRPM", leftRPM)
        Logger.recordOutput("Shooter/RightSide/SetpointRPM", rightRPM)
        val leftRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(leftRPM)
        val rightRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(rightRPM)
        leftPID.setpoint = leftRadPerSec
        rightPID.setpoint = rightRadPerSec
    }

    fun setVoltage(leftVolts: Double, rightVolts: Double) {
        leftSide.setVoltage(leftVolts)
        rightSide.setVoltage(rightVolts)
    }

    fun stop() {
        setSpeeds(0.0, 0.0)
    }

    object Commands {
        fun setVoltage(leftVolts: Double, rightVolts: Double) = InstantCommand(Shooter) { Shooter.setVoltage(leftVolts, rightVolts) }
        fun stop() = InstantCommand(Shooter) { Shooter.stop() }

        private var lastSpeedsRanAt = 6000.0 to 4000.0
        fun runAtSpeeds(rpmFast: Double = 6000.0, rpmSlow: Double = 4000.0) = SimpleCommand(
            requirements = setOf(Shooter),
            execute = {
                when (RobotPosition.getSpeakerSide()) {
                    RobotPosition.SpeakerSide.LEFT -> rpmFast to rpmSlow
                    RobotPosition.SpeakerSide.RIGHT -> rpmSlow to rpmFast
                    RobotPosition.SpeakerSide.CENTER -> null
                }?.let { lastSpeedsRanAt = it }

                val (leftSpeed, rightSpeed) = lastSpeedsRanAt
                Shooter.setSpeeds(leftSpeed, rightSpeed)
            },
            end = { Shooter.stop() }
        )
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
            additionalQualifier = side,
            logName = "Shooter",
            gearRatio = 0.5,
            simJkgMetersSquared = 0.003
        )
    }

    fun getSysIdTests() = leftSide.getSysIdTests()
}