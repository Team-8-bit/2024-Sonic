package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.trajectory.TrapezoidProfile
import org.littletonrobotics.junction.Logger
import org.team9432.Robot.applyFlip
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.withSign

class DriveToPosition(
    private val position: Pose2d,
    private val maxSpeedMetersPerSecond: Double = 4.0,
    private val maxAccelerationMetersPerSecond: Double = 3.0,
    private val positionalTolerance: Double = 0.05, // Meters
    private val rotationalTolerance: Double = 3.0, // Degrees
    private val maxRotationalSpeedDegreesPerSecond: Double = 360.0,
    private val maxRotationalAccelerationDegreesPerSecond: Double = 360.0,
): KCommand() {
    override val requirements = setOf(Drivetrain)

    private val positionPid = ProfiledPIDController(5.0, 0.0, 0.0, TrapezoidProfile.Constraints(maxSpeedMetersPerSecond, maxAccelerationMetersPerSecond))
    private var rotationPid = ProfiledPIDController(0.06, 0.0, 0.0, TrapezoidProfile.Constraints(maxRotationalSpeedDegreesPerSecond, maxRotationalAccelerationDegreesPerSecond))

    private var finalPosition = position.applyFlip()

    init {
        rotationPid.enableContinuousInput(-180.0, 180.0)
    }

    override fun initialize() {
        finalPosition = position.applyFlip()
        Logger.recordOutput("Drive/PositionGoal", finalPosition)

        val (xDistance, yDistance) = getDistanceToGoal()
        positionPid.reset(hypot(xDistance, yDistance))
        rotationPid.reset(Gyro.getYaw().degrees)

        positionPid.setGoal(0.0)
        rotationPid.setGoal(finalPosition.rotation.degrees)
    }

    override fun execute() {
        val (xDistance, yDistance) = getDistanceToGoal()
        val measurement = hypot(xDistance, yDistance)

        val result = positionPid.calculate(measurement, 0.0)

        // The x and y ratio as numbers from -1 to 1
        val xSpeed: Double
        val ySpeed: Double

        if (abs(xDistance) > abs(yDistance)) {
            xSpeed = 1.0
            ySpeed = abs(yDistance) / abs(xDistance)
        } else {
            xSpeed = abs(xDistance) / abs(yDistance)
            ySpeed = 1.0
        }

        val speeds = ChassisSpeeds.fromFieldRelativeSpeeds(
            (xSpeed * result).withSign(xDistance),
            (ySpeed * result).withSign(yDistance),
            rotationPid.calculate(Gyro.getYaw().degrees),
            Gyro.getYaw()
        )

        Drivetrain.setSpeeds(speeds)
    }

    override fun isFinished(): Boolean {
        val (xDistance, yDistance) = getDistanceToGoal()
        return hypot(xDistance, yDistance) < positionalTolerance && abs(rotationPid.positionError) < rotationalTolerance
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }

    private fun getDistanceToGoal(): Pair<Double, Double> {
        val currentPose = Drivetrain.getPose()
        val xDistance = finalPosition.x - currentPose.x
        val yDistance = finalPosition.y - currentPose.y
        return xDistance to yDistance
    }
}