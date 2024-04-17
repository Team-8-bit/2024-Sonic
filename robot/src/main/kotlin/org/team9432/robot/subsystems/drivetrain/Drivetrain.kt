package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.wpilibj.DriverStation
import org.littletonrobotics.junction.LOOP_PERIOD
import org.littletonrobotics.junction.Logger
import org.team9432.lib.SysIdUtil
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.unit.*
import org.team9432.lib.util.SwerveUtil
import org.team9432.robot.sensors.gyro.Gyro


object Drivetrain: KSubsystem() {
    val modules = ModuleConfig.entries.map { Module(it) }

    val kinematics: SwerveDriveKinematics
    private val poseEstimator: SwerveDrivePoseEstimator

    init {
        kinematics = SwerveDriveKinematics(*SwerveUtil.getMk4iModuleTranslations(26.0))
        poseEstimator = SwerveDrivePoseEstimator(kinematics, Rotation2d(), getModulePositions().toTypedArray(), Pose2d())
        for (m in modules) m.setBrakeMode(true)
    }

    override fun periodic() {
        modules.forEach(Module::periodic)

        if (DriverStation.isDisabled()) {
            modules.forEach(Module::stop)

            Logger.recordOutput("SwerveStates/Setpoints", *emptyArray<SwerveModuleState>())
            Logger.recordOutput("SwerveStates/SetpointsOptimized", *emptyArray<SwerveModuleState>())
        }

        poseEstimator.update(Gyro.getYaw(), getModulePositions().toTypedArray())

        Logger.recordOutput("Drive/Odometry", getPose())
        Logger.recordOutput("Drive/RealStates", *getModuleStates().toTypedArray())
    }

    fun setSpeeds(speeds: ChassisSpeeds) {
        val discreteSpeeds = SwerveUtil.correctForDynamics(speeds, LOOP_PERIOD.inSeconds)
        val targetStates = kinematics.toSwerveModuleStates(discreteSpeeds)
        SwerveDriveKinematics.desaturateWheelSpeeds(targetStates, 5.0)

        // Send setpoints to modules
        val optimizedSetpointStates = arrayOfNulls<SwerveModuleState>(4)
        for (i in modules.indices) {
            // The module returns the optimized state, useful for logging
            optimizedSetpointStates[i] = modules[i].runSetpoint(targetStates[i])
        }

        Logger.recordOutput("Drive/Setpoints", *targetStates)
        Logger.recordOutput("Drive/SetpointsOptimized", *optimizedSetpointStates)
    }

    fun resetPosition(pose: Pose2d, angle: Rotation2d) {
        poseEstimator.resetPosition(angle, getModulePositions().toTypedArray(), pose)
    }

    fun setVisionStandardDeviations(xyDeviation: Length) {
        poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(xyDeviation.inMeters, xyDeviation.inMeters, 20.0.degrees.inDegrees))
    }

    fun addVisionMeasurement(pose: Pose2d, timestamp: Double) {
        poseEstimator.addVisionMeasurement(pose, timestamp)
    }

    fun stop() = setSpeeds(ChassisSpeeds())
    fun stopAndX() {
        val headings = listOf(
            -45.0.degrees.asRotation2d,
            45.0.degrees.asRotation2d,
            45.0.degrees.asRotation2d,
            -45.0.degrees.asRotation2d
        )
        kinematics.resetHeadings(*headings.toTypedArray())
        stop()
    }

    fun getPose(): Pose2d = poseEstimator.estimatedPosition

    fun getRobotRelativeSpeeds() = kinematics.toChassisSpeeds(*getModuleStates().toTypedArray())
    fun getFieldRelativeSpeeds() = ChassisSpeeds.fromRobotRelativeSpeeds(getRobotRelativeSpeeds(), Gyro.getYaw())

    fun getModulePositions() = modules.map { it.position }
    fun getModuleStates() = modules.map { it.state }

    fun getSysIdTests() = SysIdUtil.getSysIdTests { volts -> modules.forEach { it.runCharacterization(volts) } }

    object Commands {
        fun stop() = InstantCommand(Drivetrain) { Drivetrain.stop() }
        fun stopAndX() = InstantCommand(Drivetrain) { Drivetrain.stopAndX() }
    }
}
