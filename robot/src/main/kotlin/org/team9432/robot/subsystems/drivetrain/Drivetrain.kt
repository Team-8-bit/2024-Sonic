package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.DriverStation
import org.littletonrobotics.junction.Logger
import org.team9432.LOOP_PERIOD_SECS
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.util.SwerveUtil
import org.team9432.robot.subsystems.gyro.Gyro
import org.team9432.robot.subsystems.vision.Vision
import kotlin.math.abs


object Drivetrain: KSubsystem() {
    val modules = ModuleIO.Module.entries.map { Module(it) }

    private val angleController =
        ProfiledPIDController(0.06, 0.0, 0.0, TrapezoidProfile.Constraints(360.0, 360.0 * 360.0))

    private val xController = PIDController(3.0, 0.0, 0.0)
    private val yController = PIDController(3.0, 0.0, 0.0)

    val kinematics: SwerveDriveKinematics
    private val poseEstimator: SwerveDrivePoseEstimator

    var apriltagStrategy = ApriltagStrategy.WHILE_NOT_MOVING

    init {
        angleController.enableContinuousInput(-180.0, 180.0)
        angleController.setTolerance(0.0)
        xController.setTolerance(0.0)
        yController.setTolerance(0.0)

        kinematics = SwerveDriveKinematics(*MODULE_TRANSLATIONS)
        poseEstimator = SwerveDrivePoseEstimator(
            kinematics, Rotation2d(), getModulePositions().toTypedArray(), Pose2d(),
            VecBuilder.fill(Units.inchesToMeters(3.0), Units.inchesToMeters(3.0), Math.toDegrees(4.0)),
            VecBuilder.fill(Units.inchesToMeters(8.0), Units.inchesToMeters(8.0), Math.toDegrees(20.0))
        )
        for (m in modules) m.setBrakeMode(true)
    }

    override fun periodic() {
        modules.forEach(Module::periodic)

        if (DriverStation.isDisabled()) {
            modules.forEach(Module::stop)

            Logger.recordOutput("SwerveStates/Setpoints", *emptyArray<SwerveModuleState>())
            Logger.recordOutput("SwerveStates/SetpointsOptimized", *emptyArray<SwerveModuleState>())
        }

        // Read wheel positions and deltas from each module
        val modulePositions = getModulePositions()

        val speeds = getSpeeds()

        Vision.getEstimatedPose2d()?.let { (pose, timestamp) ->
//            when (apriltagStrategy) {
//                ApriltagStrategy.WHILE_NOT_MOVING -> {
            if ((maxOf(
                    abs(speeds.vxMetersPerSecond),
                    abs(speeds.vyMetersPerSecond)
                ) < 0.5) && abs(Math.toDegrees(speeds.omegaRadiansPerSecond)) < 10.0
            ) {
                Logger.recordOutput("Drive/UsingVision", true)
                poseEstimator.addVisionMeasurement(pose, timestamp)
            } else {
                Logger.recordOutput("Drive/UsingVision", false)
            }
//                }

//                ApriltagStrategy.ALWAYS -> {
//                    poseEstimator.addVisionMeasurement(pose, timestamp)
//                    Logger.recordOutput("Drive/UsingVision", true)
//                }
//            }
        }

        poseEstimator.update(Gyro.getYaw(), modulePositions.toTypedArray())

        Logger.recordOutput("Drive/Odometry", getPose())
        Logger.recordOutput("Drive/RealStates", *getModuleStates().toTypedArray())
        Logger.recordOutput("Drive/AprilTagStrategy", apriltagStrategy)
    }

    fun resetPosition(pose: Pose2d, angle: Rotation2d) {
        poseEstimator.resetPosition(angle, getModulePositions().toTypedArray(), pose)
    }

    fun setSpeeds(speeds: ChassisSpeeds) {
        val discreteSpeeds = SwerveUtil.correctForDynamics(speeds, LOOP_PERIOD_SECS)
        val targetStates = kinematics.toSwerveModuleStates(discreteSpeeds)
        SwerveDriveKinematics.desaturateWheelSpeeds(targetStates, 6.0)

        // Send setpoints to modules
        val optimizedSetpointStates = arrayOfNulls<SwerveModuleState>(4)
        for (i in modules.indices) {
            // The module returns the optimized state, useful for logging
            optimizedSetpointStates[i] = modules[i].runSetpoint(targetStates[i])
        }

        Logger.recordOutput("Drive/Setpoints", *targetStates)
        Logger.recordOutput("Drive/SetpointsOptimized", *optimizedSetpointStates)
    }

    const val POSITIONAL_TOLERANCE = 0.05 // Meters
    const val ROTATIONAL_TOLERANCE = 3.0 // Degrees

    fun setPositionGoal(pose: Pose2d) {
        Logger.recordOutput("Drive/PositionGoal", pose); setXGoal(pose.x); setYGoal(pose.y); setAngleGoal(pose.rotation)
    }

    fun calculatePositionSpeed() = ChassisSpeeds.fromFieldRelativeSpeeds(
        calculateXSpeed(),
        calculateYSpeed(),
        calculateAngleSpeed(),
        Gyro.getYaw()
    )

    fun atPositionGoal(
        positionalTolerance: Double = POSITIONAL_TOLERANCE,
        rotationalTolerance: Double = ROTATIONAL_TOLERANCE,
    ) = atXGoal(positionalTolerance) && atYGoal(positionalTolerance) && atAngleGoal(rotationalTolerance)

    fun setXGoal(pose: Double) = xController.setSetpoint(pose)
    fun calculateXSpeed() = xController.calculate(getPose().x)
    fun atXGoal(tolerance: Double = POSITIONAL_TOLERANCE) = abs(xController.positionError) < tolerance

    fun setYGoal(pose: Double) = yController.setSetpoint(pose)
    fun calculateYSpeed() = yController.calculate(getPose().y)
    fun atYGoal(tolerance: Double = POSITIONAL_TOLERANCE) = abs(yController.positionError) < tolerance

    fun setAngleGoal(angle: Rotation2d) = angleController.setGoal(angle.degrees)
    fun calculateAngleSpeed() = angleController.calculate(Gyro.getYaw().degrees)
    fun atAngleGoal(tolerance: Double = ROTATIONAL_TOLERANCE) = abs(angleController.positionError) < tolerance

    fun stop() = setSpeeds(ChassisSpeeds())
    fun stopAndX() {
        val headings = listOf(
            Rotation2d.fromDegrees(-45.0),
            Rotation2d.fromDegrees(45.0),
            Rotation2d.fromDegrees(45.0),
            Rotation2d.fromDegrees(-45.0)
        )
        kinematics.resetHeadings(*headings.toTypedArray())
        stop()
    }

    fun resetAngleController(angle: Rotation2d = Gyro.getYaw()) = angleController.reset(angle.degrees)

    fun getPose(): Pose2d = poseEstimator.estimatedPosition

    fun getSpeeds() = kinematics.toChassisSpeeds(*getModuleStates().toTypedArray())

    fun getModulePositions() = modules.map { it.position }
    fun getModuleStates() = modules.map { it.state }

    private val MODULE_TRANSLATIONS: Array<Translation2d>
        get() {
            val moduleDistance = Units.inchesToMeters(14.67246)
            val frontLeft = Translation2d(moduleDistance, moduleDistance)
            val frontRight = Translation2d(moduleDistance, -moduleDistance)
            val backLeft = Translation2d(-moduleDistance, moduleDistance)
            val backRight = Translation2d(-moduleDistance, -moduleDistance)
            return arrayOf(frontLeft, frontRight, backLeft, backRight)
        }

    enum class ApriltagStrategy {
        WHILE_NOT_MOVING,
        ALWAYS
    }
}
