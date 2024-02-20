package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.filter.SlewRateLimiter
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.RobotState
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.drivers.gyro.GyroIO
import org.team9432.lib.drivers.gyro.GyroIOPigeon2
import org.team9432.lib.drivers.gyro.GyroIOSim
import org.team9432.lib.drivers.gyro.LoggedGyroIOInputs
import org.team9432.lib.wpilib.ChassisSpeeds
import org.team9432.robot.DrivetrainConstants
import org.team9432.robot.DrivetrainConstants.AngleConstants
import org.team9432.robot.DrivetrainConstants.MAX_ACCELERATION_METERS_PER_SECOND_SQUARED
import org.team9432.robot.DrivetrainConstants.MODULE_TRANSLATIONS
import org.team9432.robot.DrivetrainConstants.PoseConstants
import kotlin.math.abs

object Drivetrain: KSubsystem() {
    private val moduleInputs = List(4) { LoggedModuleIOInputs() }
    private val modules: List<ModuleIO> = when (Robot.mode) {
        Robot.Mode.REAL, REPLAY -> ModuleIO.Module.entries.map { ModuleIONEO(it) }
        SIM -> ModuleIO.Module.entries.map { ModuleIOSim(it) }
    }

    private val gyroInputs = LoggedGyroIOInputs()
    private val gyro: GyroIO = when (Robot.mode) {
        REAL, REPLAY -> GyroIOPigeon2()
        SIM -> GyroIOSim()
    }

    private val angleController = ProfiledPIDController(AngleConstants.P, AngleConstants.I, AngleConstants.D, AngleConstants.CONTROLLER_CONSTRAINTS)

    private val xController = PIDController(PoseConstants.P, PoseConstants.I, PoseConstants.D)
    private val yController = PIDController(PoseConstants.P, PoseConstants.I, PoseConstants.D)

    private val xLimiter = SlewRateLimiter(MAX_ACCELERATION_METERS_PER_SECOND_SQUARED)
    private val yLimiter = SlewRateLimiter(MAX_ACCELERATION_METERS_PER_SECOND_SQUARED)

    private val kinematics: SwerveDriveKinematics
    private val poseEstimator: SwerveDrivePoseEstimator

    private var targetStates = getModuleStates()

    private var manualSpeeds = ChassisSpeeds()

    init {
        mode = SubsystemMode.MANUAL
        angleController.enableContinuousInput(-180.0, 180.0)
        angleController.setTolerance(0.0)
        xController.setTolerance(0.0)
        yController.setTolerance(0.0)
        xLimiter.reset(0.0)
        yLimiter.reset(0.0)
        kinematics = SwerveDriveKinematics(*MODULE_TRANSLATIONS)
        poseEstimator = SwerveDrivePoseEstimator(
            kinematics, Rotation2d.fromDegrees(yaw), getModulePositions().toTypedArray(), Pose2d(), VecBuilder.fill(
                Units.inchesToMeters(3.0), Units.inchesToMeters(3.0), Math.toDegrees(4.0)
            ), VecBuilder.fill(
                Units.inchesToMeters(0.0), Units.inchesToMeters(0.0), Math.toDegrees(0.0)
            )
        )
        for (m in modules) m.setBrakeMode(true)
    }

    override fun constantPeriodic() {
        for (i in modules.indices) {
            modules[i].updateInputs(moduleInputs[i])
            Logger.processInputs(("Drive/" + modules[i].module.name) + "_Module", moduleInputs[i])

            if (RobotState.isDisabled()) modules[i].updateIntegratedEncoder()
        }
        gyro.updateInputs(gyroInputs)
        Logger.processInputs("Gyro", gyroInputs)

        poseEstimator.update(Rotation2d.fromDegrees(yaw), getModulePositions().toTypedArray())

        Logger.recordOutput("Odometry", getPose())
        Logger.recordOutput("Drive/RealStates", *getModuleStates().toTypedArray())
        Logger.recordOutput("Drive/TargetStates", *targetStates.toTypedArray())

        if (Robot.mode == SIM) {
            gyro.setYaw(gyroInputs.yaw + Math.toDegrees(kinematics.toChassisSpeeds(*targetStates.toTypedArray()).omegaRadiansPerSecond) * Robot.period)
        }
    }

    override fun manualPeriodic() {
        if (isNotMoving()) {
            x()
        } else {
            setSpeeds(manualSpeeds)
        }
    }

    private fun isNotMoving() = abs(manualSpeeds.vxMetersPerSecond) < 0.5 && abs(manualSpeeds.vyMetersPerSecond) < 0.5 && abs(Math.toDegrees(manualSpeeds.omegaRadiansPerSecond)) < 5

    override fun PIDPeriodic() {
        val currentPose = getPose()
        val vx = xController.calculate(currentPose.x)
        val vy = yController.calculate(currentPose.y)
        val va = Math.toRadians(angleController.calculate(currentPose.rotation.degrees))

        setSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(vx, vy, va, yaw))
    }

    override fun disabledPeriodic() {
        modules.forEach { it.disabled = true }
    }

    private fun setPositionGoal(pose2d: Pose2d) {
        xController.setpoint = pose2d.x
        yController.setpoint = pose2d.y
        angleController.setGoal(pose2d.rotation.degrees)
    }

    private fun setSpeeds(speeds: ChassisSpeeds) {
        speeds.vxMetersPerSecond = xLimiter.calculate(speeds.vxMetersPerSecond)
        speeds.vyMetersPerSecond = yLimiter.calculate(speeds.vyMetersPerSecond)
        targetStates = kinematics.toSwerveModuleStates(speeds).toList()
        setSwerveModules(targetStates)
    }

    private fun getModulePositions() = moduleInputs.map { SwerveModulePosition(it.positionMeters, Rotation2d.fromDegrees(it.angle)) }

    private fun getModuleStates() = moduleInputs.map { SwerveModuleState(it.speedMetersPerSecond, Rotation2d.fromDegrees(it.angle)) }

    fun resetGyro() = gyro.setYaw(0.0)

    private fun setSwerveModules(states: List<SwerveModuleState>) {
        for (i in modules.indices) modules[i].setState(states[i])
    }

    private fun x() {
        setSwerveModules(listOf(
            SwerveModuleState(0.0, Rotation2d.fromDegrees(-45.0)),
            SwerveModuleState(0.0, Rotation2d.fromDegrees(45.0)),
            SwerveModuleState(0.0, Rotation2d.fromDegrees(45.0)),
            SwerveModuleState(0.0, Rotation2d.fromDegrees(-45.0)),
        ))
    }

    private fun getPose(): Pose2d = poseEstimator.estimatedPosition

    private var yaw: Double
        get() = gyroInputs.yaw
        set(angle) {
            gyro.setYaw(angle)
            angleController.reset(angle)
        }

    fun fieldOrientedDriveCommand(
        xJoystickInput: () -> Double,
        yJoystickInput: () -> Double,
        angleJoystickInput: () -> Double,
        maxSpeedMetersPerSecond: Double = DrivetrainConstants.MAX_VELOCITY_METERS_PER_SECOND,
        maxSpeedDegreesPerSecond: Double = DrivetrainConstants.MAX_ANGULAR_SPEED_DEGREES_PER_SECOND,
    ) = SimpleCommand(
        execute = {
            val xSpeed = xJoystickInput.invoke() * maxSpeedMetersPerSecond
            val ySpeed = yJoystickInput.invoke() * maxSpeedMetersPerSecond
            val radiansPerSecond = Math.toRadians(angleJoystickInput.invoke() * maxSpeedDegreesPerSecond)
            manualSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, radiansPerSecond, yaw)
        },
        end = { manualSpeeds = ChassisSpeeds(0.0, 0.0, 0.0) },
        isFinished = { false },
        requirements = mutableSetOf(Drivetrain),
        initialize = { mode = SubsystemMode.MANUAL }
    )
}
