package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap
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
import org.team9432.lib.logged.neo.LoggedNeoIO
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.RobotPosition
import kotlin.math.abs


object Shooter: KSubsystem() {
    private val leftSide = LoggedNeo(getConfig(Devices.LEFT_SHOOTER_ID, false, "Left"))
    private val rightSide = LoggedNeo(getConfig(Devices.RIGHT_SHOOTER_ID, true, "Right"))

    private val feedforward: SimpleMotorFeedforward

    private val leftPID = PIDController(0.0, 0.0, 0.0)
    private val rightPID = PIDController(0.0, 0.0, 0.0)

    private val fastDistanceSpeedMap = InterpolatingDoubleTreeMap()
    private val slowDistanceSpeedMap = InterpolatingDoubleTreeMap()

    private var mode: Mode = Mode.STOPPED

    enum class Mode {
        PID, STOPPED
    }

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

        leftPID.setTolerance(Units.rotationsPerMinuteToRadiansPerSecond(100.0))
        rightPID.setTolerance(Units.rotationsPerMinuteToRadiansPerSecond(100.0))

        fastDistanceSpeedMap.put(1.0, 3000.0)
        fastDistanceSpeedMap.put(1.5, 4500.0)
        fastDistanceSpeedMap.put(2.0, 4500.0)
        fastDistanceSpeedMap.put(3.0, 4500.0)
        fastDistanceSpeedMap.put(4.0, 4500.0)

        slowDistanceSpeedMap.put(1.0, 2500.0)
        slowDistanceSpeedMap.put(1.5, 2500.0)
        slowDistanceSpeedMap.put(2.0, 2500.0)
        slowDistanceSpeedMap.put(3.0, 2500.0)
        slowDistanceSpeedMap.put(4.0, 2500.0)
    }

    private var leftInputs = LoggedNeoIO.NEOIOInputs()
    private var rightInputs = LoggedNeoIO.NEOIOInputs()
    override fun periodic() {
        leftInputs = leftSide.updateAndRecordInputs()
        rightInputs = rightSide.updateAndRecordInputs()

        if (mode == Mode.PID) {
            val feedforwardVolts = feedforward.calculate(leftPID.setpoint)
            leftSide.setVoltage(leftPID.calculate(leftInputs.velocityRadPerSec) + feedforwardVolts)
            rightSide.setVoltage(rightPID.calculate(rightInputs.velocityRadPerSec) + feedforward.calculate(rightPID.setpoint))
            Logger.recordOutput("Shooter/FFCalc", feedforwardVolts)
        }

        Logger.recordOutput("Shooter/LeftSide/RPM", Units.radiansPerSecondToRotationsPerMinute(leftInputs.velocityRadPerSec))
        Logger.recordOutput("Shooter/RightSide/RPM", Units.radiansPerSecondToRotationsPerMinute(rightInputs.velocityRadPerSec))
        if (Robot.isTest) {
            Logger.recordOutput("Shooter/LeftSide/PositionRadians", leftInputs.angle.radians)
            Logger.recordOutput("Shooter/RightSide/PositionRadians", rightInputs.angle.radians)
        }
    }

    fun setSpeeds(leftRPM: Double, rightRPM: Double) {
        mode = Mode.PID
        Logger.recordOutput("Shooter/LeftSide/SetpointRPM", leftRPM)
        Logger.recordOutput("Shooter/RightSide/SetpointRPM", rightRPM)
        val leftRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(leftRPM)
        val rightRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(rightRPM)
        leftPID.setpoint = leftRadPerSec
        rightPID.setpoint = rightRadPerSec
    }

    fun stop() {
        mode = Mode.STOPPED
        leftSide.stop()
        rightSide.stop()
    }

    private var currentShooterDirection = ShooterDirection.LEFT_FAST

    enum class ShooterDirection {
        LEFT_FAST, RIGHT_FAST
    }

    object Commands {
        fun stop() = InstantCommand(Shooter) { Shooter.stop() }

        fun runAtSpeeds() = SimpleCommand(
            requirements = setOf(Shooter),
            end = { Shooter.stop() },
            execute = {
                val distanceToSpeaker = RobotPosition.distanceToSpeaker()
                val rpmFast = fastDistanceSpeedMap.get(distanceToSpeaker)
                val rpmSlow = slowDistanceSpeedMap.get(distanceToSpeaker)

                when (RobotPosition.getSpeakerSide()) {
                    RobotPosition.SpeakerSide.LEFT -> ShooterDirection.LEFT_FAST
                    RobotPosition.SpeakerSide.RIGHT -> ShooterDirection.RIGHT_FAST
                    RobotPosition.SpeakerSide.CENTER -> null
                }?.let { currentShooterDirection = it }

                when (currentShooterDirection) {
                    ShooterDirection.LEFT_FAST -> Shooter.setSpeeds(rpmFast, rpmSlow)
                    ShooterDirection.RIGHT_FAST -> Shooter.setSpeeds(rpmSlow, rpmFast)
                }
            }
        )

        fun runAtFastSpeeds() = SimpleCommand(
            requirements = setOf(Shooter),
            end = { Shooter.stop() },
            execute = {
                Shooter.setSpeeds(10000.0, 6000.0)
            }
        )
    }

    private val velocitySetpointTolerance = Units.rotationsPerMinuteToRadiansPerSecond(250.0)
    fun atSetpoint(): Boolean {
        return abs(leftPID.setpoint - leftInputs.velocityRadPerSec) < velocitySetpointTolerance
                && abs(rightPID.setpoint - rightInputs.velocityRadPerSec) < velocitySetpointTolerance
    }

    private fun getConfig(canID: Int, inverted: Boolean, side: String): LoggedNeo.Config {
        return LoggedNeo.Config(
            canID = canID,
            motorType = Spark.MotorType.VORTEX,
            deviceName = "$side Shooter",
            sparkConfig = Spark.Config(
                inverted = inverted,
                idleMode = CANSparkBase.IdleMode.kCoast,
                stallCurrentLimit = 60
            ),
            additionalQualifier = side,
            logName = "Shooter",
            gearRatio = 0.5,
            simJkgMetersSquared = 0.003
        )
    }

    fun getSysIdTests() = leftSide.getSysIdTests()
}