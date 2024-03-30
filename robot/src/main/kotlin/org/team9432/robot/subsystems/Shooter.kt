package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import edu.wpi.first.math.util.Units
import edu.wpi.first.units.Units.Volts
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.lib.KSysIdConfig
import org.team9432.lib.KSysIdMechanism
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.motors.neo.LoggedNeo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.RobotPosition


object Shooter: KSubsystem() {
    private val leftSide = LoggedNeo(getConfig(Devices.LEFT_SHOOTER_ID, false, "Left"))
    private val rightSide = LoggedNeo(getConfig(Devices.RIGHT_SHOOTER_ID, true, "Right"))

    private var isRunningAtSpeeds: Pair<Int, Int>? = null

    private val sysId: SysIdRoutine

    init {
        when (State.mode) {
            REAL, REPLAY -> {
                leftSide.setPID(0.0005, 0.0, 0.0)
                rightSide.setPID(0.0005, 0.0, 0.0)
            }

            SIM -> {
                leftSide.setPID(0.1, 0.0, 0.0)
                rightSide.setPID(0.1, 0.0, 0.0)
            }
        }

        sysId = SysIdRoutine(
            KSysIdConfig(
                recordState = { state -> Logger.recordOutput("SysIdState", state.toString()) }
            ),
            KSysIdMechanism(
                { volts -> leftSide.setVoltage(volts.`in`(Volts)) },
                name = "Left Shooter"
            )
        )
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
            leftSide.setSpeed(leftSpeed)
            rightSide.setSpeed(rightSpeed)
        }

        Logger.recordOutput("Shooter/LeftSide/RPM", Units.radiansPerSecondToRotationsPerMinute(leftInputs.velocityRadPerSec))
        Logger.recordOutput("Shooter/RightSide/RPM", Units.radiansPerSecondToRotationsPerMinute(rightInputs.velocityRadPerSec))
        if (Robot.isTest) {
            Logger.recordOutput("Shooter/LeftSide/PositionRadians", Units.radiansPerSecondToRotationsPerMinute(leftInputs.angle.radians))
            Logger.recordOutput("Shooter/RightSide/PositionRadians", Units.radiansPerSecondToRotationsPerMinute(rightInputs.angle.radians))
        }
    }

    fun startRunAtSpeeds(rpmFast: Int = 6000, rpmSlow: Int = 4000) {
        isRunningAtSpeeds = rpmFast to rpmSlow
    }

    fun setVoltage(leftVolts: Double, rightVolts: Double) {
        isRunningAtSpeeds = null
        leftSide.setVoltage(leftVolts)
        rightSide.setVoltage(rightVolts)
    }

    fun setSpeed(leftRPM: Int, rightRPM: Int) {
        isRunningAtSpeeds = null
        leftSide.setSpeed(leftRPM)
        rightSide.setSpeed(rightRPM)
    }

    fun stop() {
        isRunningAtSpeeds = null
        leftSide.stop()
        rightSide.stop()
    }

    object Commands {
        fun setVoltage(leftVolts: Double, rightVolts: Double) = InstantCommand(Shooter) { Shooter.setVoltage(leftVolts, rightVolts) }
        fun setSpeed(leftRPM: Int, rightRPM: Int) = InstantCommand(Shooter) { Shooter.setSpeed(leftRPM, rightRPM) }
        fun stop() = InstantCommand(Shooter) { Shooter.stop() }

        fun startRunAtSpeeds(rpmFast: Int = 6000, rpmSlow: Int = 4000) = InstantCommand(Shooter) { Shooter.startRunAtSpeeds(rpmFast, rpmSlow) }
    }

    private fun getConfig(canID: Int, inverted: Boolean, side: String): LoggedNeo.Config {
        return LoggedNeo.Config(
            canID = canID,
            motorType = Spark.MotorType.VORTEX,
            motorName = "$side Shooter",
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

    fun sysIdQuasistatic(direction: SysIdRoutine.Direction) = sysId.quasistatic(direction)
    fun sysIdDynamic(direction: SysIdRoutine.Direction) = sysId.dynamic(direction)
}