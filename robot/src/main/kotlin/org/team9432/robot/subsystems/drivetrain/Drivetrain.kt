package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.DriverStation
import org.littletonrobotics.junction.Logger
import org.team9432.LOOP_PERIOD_SECS
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.drivers.gyro.GyroIO
import org.team9432.lib.drivers.gyro.GyroIOPigeon2
import org.team9432.lib.drivers.gyro.LoggedGyroIOInputs
import org.team9432.lib.util.SwerveUtil
import org.team9432.lib.wpilib.ChassisSpeeds
import org.team9432.robot.subsystems.vision.Vision
import kotlin.math.abs


object Drivetrain: KSubsystem() {
    private val modules = ModuleIO.Module.entries.map { Module(it) }

    private val gyroInputs = LoggedGyroIOInputs()
    private val gyro: GyroIO = when (Robot.mode) {
        REAL, REPLAY -> GyroIOPigeon2()
        SIM -> object: GyroIO {}
    }

    private val angleController = ProfiledPIDController(5.0, 0.0, 0.0, TrapezoidProfile.Constraints(360.0, 360.0 * 360.0))

    private val xController = PIDController(3.0, 0.0, 0.0)
    private val yController = PIDController(3.0, 0.0, 0.0)

    private val kinematics: SwerveDriveKinematics
    private val poseEstimator: SwerveDrivePoseEstimator

    private var rawGyroRotation = Rotation2d()

    private val lastModulePositions = MutableList(4) { SwerveModulePosition() }

    init {
        angleController.enableContinuousInput(-180.0, 180.0)
        angleController.setTolerance(0.0)
        xController.setTolerance(0.0)
        yController.setTolerance(0.0)

        kinematics = SwerveDriveKinematics(*MODULE_TRANSLATIONS)
        poseEstimator = SwerveDrivePoseEstimator(
            kinematics, Rotation2d.fromDegrees(yaw), lastModulePositions.toTypedArray(), Pose2d(),
            VecBuilder.fill(Units.inchesToMeters(3.0), Units.inchesToMeters(3.0), Math.toDegrees(4.0)),
            VecBuilder.fill(Units.inchesToMeters(6.0), Units.inchesToMeters(6.0), Math.toDegrees(10.0))
        )
        for (m in modules) m.setBrakeMode(true)
    }

    override fun periodic() {
        gyro.updateInputs(gyroInputs)
        Logger.processInputs("Gyro", gyroInputs)

        modules.forEach(Module::periodic)

        if (DriverStation.isDisabled()) {
            modules.forEach(Module::stop)

            Logger.recordOutput("SwerveStates/Setpoints", *emptyArray<SwerveModuleState>())
            Logger.recordOutput("SwerveStates/SetpointsOptimized", *emptyArray<SwerveModuleState>())
        }

        // Read wheel positions and deltas from each module
        val modulePositions = getModulePositions()

        // Update gyro angle
        if (gyroInputs.connected) {
            rawGyroRotation = gyroInputs.yaw
        } else {
            val moduleDeltas = arrayOfNulls<SwerveModulePosition>(4)

            for (i in modules.indices) {
                moduleDeltas[i] = SwerveModulePosition(
                    modulePositions[i].distanceMeters - lastModulePositions[i].distanceMeters,
                    modulePositions[i].angle
                )
                lastModulePositions[i] = modulePositions[i]
            }

            // Use the angle delta from the kinematics and module deltas
            val twist = kinematics.toTwist2d(*moduleDeltas)
            rawGyroRotation = rawGyroRotation.plus(Rotation2d(twist.dtheta))
        }

        Vision.getEstimatedPose2d()?.let {
            poseEstimator.addVisionMeasurement(it.first, it.second)
        }

        poseEstimator.update(rawGyroRotation, modulePositions.toTypedArray())

        Logger.recordOutput("Drive/Odometry", getPose())
        Logger.recordOutput("Drive/RealStates", *getModuleStates().toTypedArray())
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

    private const val POSITIONAL_TOLERANCE = 0.05 // Meters
    private const val ROTATIONAL_TOLERANCE = 3.0 // Degrees

    fun setPositionGoal(pose: Pose2d) {
        Logger.recordOutput("Drive/PositionGoal", pose); setXGoal(pose.x); setYGoal(pose.y); setAngleGoal(pose.rotation)
    }

    fun calculatePositionSpeed(): ChassisSpeeds = ChassisSpeeds(calculateXSpeed(), calculateYSpeed(), calculateAngleSpeed())
    fun atPositionGoal() = atXGoal() && atYGoal() && atAngleGoal()

    fun setXGoal(pose: Double) = xController.setSetpoint(pose)
    fun calculateXSpeed() = xController.calculate(getPose().x)
    fun atXGoal() = abs(xController.positionError) < POSITIONAL_TOLERANCE

    fun setYGoal(pose: Double) = yController.setSetpoint(pose)
    fun calculateYSpeed() = yController.calculate(getPose().y)
    fun atYGoal() = abs(yController.positionError) < POSITIONAL_TOLERANCE

    fun setAngleGoal(angle: Rotation2d) = angleController.setGoal(angle.degrees)
    fun calculateAngleSpeed() = angleController.calculate(yaw)
    fun atAngleGoal() = abs(angleController.positionError) < ROTATIONAL_TOLERANCE

    fun stop() = setSpeeds(ChassisSpeeds())
    fun stopAndX() {
        val headings = listOf(Rotation2d.fromDegrees(-45.0), Rotation2d.fromDegrees(45.0), Rotation2d.fromDegrees(45.0), Rotation2d.fromDegrees(-45.0))
        kinematics.resetHeadings(*headings.toTypedArray())
        stop()
    }

    fun resetGyro() = gyro.setYaw(0.0)
    fun getPose(): Pose2d = poseEstimator.estimatedPosition

    fun getSpeeds() = ChassisSpeeds.fromWPIChassisSpeeds(kinematics.toChassisSpeeds(*getModuleStates().toTypedArray()))

    var yaw: Double
        get() = rawGyroRotation.degrees
        set(angle) {
            gyro.setYaw(angle)
        }

    private fun getModulePositions() = modules.map { it.position }
    private fun getModuleStates() = modules.map { it.state }

    private val MODULE_TRANSLATIONS: Array<Translation2d>
        get() {
            val moduleDistance = Units.inchesToMeters(14.67246)
            val frontLeft = Translation2d(moduleDistance, moduleDistance)
            val frontRight = Translation2d(moduleDistance, -moduleDistance)
            val backLeft = Translation2d(-moduleDistance, moduleDistance)
            val backRight = Translation2d(-moduleDistance, -moduleDistance)
            return arrayOf(frontLeft, frontRight, backLeft, backRight)
        }
}
