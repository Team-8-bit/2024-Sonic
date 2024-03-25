package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.trajectory.TrapezoidProfile
import org.littletonrobotics.junction.Logger
import org.team9432.Robot.applyFlip
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.withSign

class DriveToPosition(
    private val position: Pose2d,
    private val maxSpeedMetersPerSecond: Double = 4.0,
    private val maxAccelerationMetersPerSecond: Double = 3.0,
    private val positionalTolerance: Double = 0.05, // Meters
    private val rotationalTolerance: Double = 3.0, // Degrees
    private val maxRotationalSpeedDegreesPerSecond: Double = 360.0,
    private val maxRotationalAccelerationDegreesPerSecond: Double = 360.0,
    private val velocityGoal: Double = 0.0,
): KCommand() {
    override val requirements = setOf(Drivetrain)

    private val positionPid = ProfiledPIDController(3.0, 0.0, 0.0, TrapezoidProfile.Constraints(maxSpeedMetersPerSecond, maxAccelerationMetersPerSecond))
    private var rotationPid = ProfiledPIDController(0.06, 0.0, 0.0, TrapezoidProfile.Constraints(maxRotationalSpeedDegreesPerSecond, maxRotationalAccelerationDegreesPerSecond))

    private var finalPosition = position.applyFlip()

    init {
        rotationPid.enableContinuousInput(-180.0, 180.0)
    }

    override fun initialize() {
        finalPosition = position.applyFlip()
        Logger.recordOutput("Drive/PositionGoal", finalPosition)

        val currentSpeeds = Drivetrain.getFieldRelativeSpeeds()

        positionPid.setTolerance(positionalTolerance)
        rotationPid.setTolerance(rotationalTolerance)

        val (xDistance, yDistance) = getDistancesToGoal()
        positionPid.reset(hypot(xDistance, yDistance))
        rotationPid.reset(Gyro.getYaw().degrees, Math.toDegrees(currentSpeeds.omegaRadiansPerSecond))

        positionPid.setGoal(TrapezoidProfile.State(0.0, velocityGoal))
        rotationPid.setGoal(finalPosition.rotation.degrees)
    }

    override fun execute() {
        val (xDistance, yDistance) = getDistancesToGoal()
        val measurement = hypot(xDistance, yDistance)

        val result = positionPid.calculate(measurement)

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
        val (xDistance, yDistance) = getDistancesToGoal()
        return hypot(xDistance, yDistance) < positionalTolerance && abs(rotationPid.positionError) < rotationalTolerance
    }

    override fun end(interrupted: Boolean) {
        if (velocityGoal == 0.0) {
            Drivetrain.stopAndX()
        }
    }

    private fun getDistancesToGoal(pose: Pose2d = Drivetrain.getPose()): Pair<Double, Double> {
        val xDistance = finalPosition.x - pose.x
        val yDistance = finalPosition.y - pose.y
        return xDistance to yDistance
    }

    private fun getSpeedToGoal(speeds: ChassisSpeeds): Double {
        val currentPose = Drivetrain.getPose()

        return abs(
            min(
                0.0,
                -Translation2d(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond)
                    .rotateBy(
                        finalPosition
                            .translation
                            .minus(currentPose.translation)
                            .angle
                            .unaryMinus()
                    ).x
            )
        )
    }
}