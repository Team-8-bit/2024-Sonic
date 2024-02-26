package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.filter.SlewRateLimiter
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
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.drivers.gyro.GyroIO
import org.team9432.lib.drivers.gyro.GyroIOPigeon2
import org.team9432.lib.drivers.gyro.LoggedGyroIOInputs
import org.team9432.lib.wpilib.ChassisSpeeds
import kotlin.math.abs
import edu.wpi.first.math.kinematics.ChassisSpeeds as WPIChassisSpeeds


object Drivetrain: KSubsystem() {
    private const val MAX_ANGULAR_SPEED_DEGREES_PER_SECOND = 360.0
    private const val MAX_VELOCITY_METERS_PER_SECOND = 5.0
    private const val MAX_ACCELERATION_METERS_PER_SECOND_SQUARED = 20.0


    private val modules = ModuleIO.Module.entries.map { Module(it) }

    private val gyroInputs = LoggedGyroIOInputs()
    private val gyro: GyroIO = when (Robot.mode) {
        REAL, REPLAY -> GyroIOPigeon2()
        SIM -> object: GyroIO {}
    }

    private val angleController = ProfiledPIDController(5.0, 0.0, 0.0, TrapezoidProfile.Constraints(360.0, 360.0 * 360.0))

    private val xController = PIDController(3.0, 0.0, 0.0)
    private val yController = PIDController(3.0, 0.0, 0.0)

    private val xLimiter = SlewRateLimiter(MAX_ACCELERATION_METERS_PER_SECOND_SQUARED)
    private val yLimiter = SlewRateLimiter(MAX_ACCELERATION_METERS_PER_SECOND_SQUARED)

    private val kinematics: SwerveDriveKinematics
    private val poseEstimator: SwerveDrivePoseEstimator

    private var manualSpeeds = ChassisSpeeds()

    private var rawGyroRotation = Rotation2d()

    private val lastModulePositions = mutableListOf(
        SwerveModulePosition(),
        SwerveModulePosition(),
        SwerveModulePosition(),
        SwerveModulePosition()
    )

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
            kinematics, Rotation2d.fromDegrees(yaw), lastModulePositions.toTypedArray(), Pose2d(), VecBuilder.fill(
                Units.inchesToMeters(3.0), Units.inchesToMeters(3.0), Math.toDegrees(4.0)
            ), VecBuilder.fill(
                Units.inchesToMeters(0.0), Units.inchesToMeters(0.0), Math.toDegrees(0.0)
            )
        )
        for (m in modules) m.setBrakeMode(true)
    }

    override fun constantPeriodic() {
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
        val moduleDeltas = arrayOfNulls<SwerveModulePosition>(4)

        for (i in modules.indices) {
            moduleDeltas[i] = SwerveModulePosition(
                modulePositions[i].distanceMeters - lastModulePositions[i].distanceMeters,
                modulePositions[i].angle
            )
            lastModulePositions[i] = modulePositions[i]
        }

        // Update gyro angle
        if (gyroInputs.connected) {
            // Use the real gyro angle
            rawGyroRotation = gyroInputs.yaw
        } else {
            // Use the angle delta from the kinematics and module deltas
            val twist = kinematics.toTwist2d(*moduleDeltas)
            rawGyroRotation = rawGyroRotation.plus(Rotation2d(twist.dtheta))
        }

        poseEstimator.update(rawGyroRotation, modulePositions.toTypedArray())

        Logger.recordOutput("Odometry", getPose())
        Logger.recordOutput("Drive/RealStates", *getModuleStates().toTypedArray())
    }

    override fun manualPeriodic() {
        setSpeeds(manualSpeeds)
    }

    private fun isNotMoving() = abs(manualSpeeds.vxMetersPerSecond) < 0.5 && abs(manualSpeeds.vyMetersPerSecond) < 0.5 && abs(Math.toDegrees(manualSpeeds.omegaRadiansPerSecond)) < 5

    override fun PIDPeriodic() {
        val currentPose = getPose()
        val vx = xController.calculate(currentPose.x)
        val vy = yController.calculate(currentPose.y)
        val va = Math.toRadians(angleController.calculate(currentPose.rotation.degrees))

        setSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(vx, vy, va, yaw))
    }

    private fun setPositionGoal(pose2d: Pose2d) {
        Logger.recordOutput("Drive/PositionGoal", pose2d)
        xController.setpoint = pose2d.x
        yController.setpoint = pose2d.y
        angleController.setGoal(pose2d.rotation.degrees)
    }

    private fun atPositionGoal(): Boolean {
        val pose = getPose()
        val rotationalTolerance = 3.0 // Degrees
        val positionalTolerance = 0.05 // Meters
        return abs(xController.setpoint - pose.x) < positionalTolerance && abs(yController.setpoint - pose.y) < positionalTolerance && abs(angleController.setpoint.position - pose.rotation.degrees) < rotationalTolerance
    }

    private fun setSpeeds(speeds: ChassisSpeeds) {
        val discreteSpeeds = WPIChassisSpeeds.discretize(speeds, Robot.period)
        val targetStates = kinematics.toSwerveModuleStates(discreteSpeeds)
        SwerveDriveKinematics.desaturateWheelSpeeds(targetStates, MAX_VELOCITY_METERS_PER_SECOND)

//        speeds.vxMetersPerSecond = xLimiter.calculate(speeds.vxMetersPerSecond)
//        speeds.vyMetersPerSecond = yLimiter.calculate(speeds.vyMetersPerSecond)

        // Send setpoints to modules
        val optimizedSetpointStates = arrayOfNulls<SwerveModuleState>(4)
        for (i in modules.indices) {
            // The module returns the optimized state, useful for logging
            optimizedSetpointStates[i] = modules[i].runSetpoint(targetStates[i])
        }

        // Log setpoint states
        Logger.recordOutput("SwerveStates/Setpoints", *targetStates)
        Logger.recordOutput("SwerveStates/SetpointsOptimized", *optimizedSetpointStates)
    }

    private fun getModulePositions() = modules.map { it.position }
    private fun getModuleStates() = modules.map { it.state }

    fun resetGyro() = gyro.setYaw(0.0)

    fun stop() = setSpeeds(ChassisSpeeds())

    private fun stopAndX() {
        val headings = listOf(
            Rotation2d.fromDegrees(-45.0),
            Rotation2d.fromDegrees(45.0),
            Rotation2d.fromDegrees(45.0),
            Rotation2d.fromDegrees(-45.0),
        )
        kinematics.resetHeadings(*headings.toTypedArray())
        stop()
    }

    private fun getPose(): Pose2d = poseEstimator.estimatedPosition

    private var yaw: Double
        get() = rawGyroRotation.degrees
        set(angle) {
            gyro.setYaw(angle)
            angleController.reset(angle)
        }

    fun driveToPositionCommand(
        position: Pose2d,
    ) = SimpleCommand(
        initialize = {
            setPositionGoal(position)
            mode = SubsystemMode.PID
        },
        requirements = setOf(Drivetrain),
        isFinished = { mode != SubsystemMode.PID || atPositionGoal() }
    )

    fun getRobotRelativeSpeeds(): ChassisSpeeds {
        return ChassisSpeeds.fromWPIChassisSpeeds(kinematics.toChassisSpeeds(*getModuleStates().toTypedArray()))
    }

    fun fieldOrientedDriveCommand(
        xJoystickInput: () -> Double,
        yJoystickInput: () -> Double,
        angleJoystickInput: () -> Double,
        maxSpeedMetersPerSecond: Double = MAX_VELOCITY_METERS_PER_SECOND,
        maxSpeedDegreesPerSecond: Double = MAX_ANGULAR_SPEED_DEGREES_PER_SECOND,
    ) = SimpleCommand(
        execute = {
            val xSpeed = xJoystickInput.invoke() * maxSpeedMetersPerSecond
            val ySpeed = yJoystickInput.invoke() * maxSpeedMetersPerSecond
            val radiansPerSecond = Math.toRadians(angleJoystickInput.invoke() * maxSpeedDegreesPerSecond)
            manualSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, radiansPerSecond, yaw)
        },
        end = { manualSpeeds = ChassisSpeeds(0.0, 0.0, 0.0) },
        isFinished = { false },
        requirements = setOf(Drivetrain),
        initialize = { mode = SubsystemMode.MANUAL }
    )

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
