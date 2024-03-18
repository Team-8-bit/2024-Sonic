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
    private val positionalTolerance: Double = Drivetrain.POSITIONAL_TOLERANCE,
    private val rotationalTolerance: Double = Drivetrain.ROTATIONAL_TOLERANCE,
): KCommand() {
    override val requirements = setOf(Drivetrain)

    private val pid = ProfiledPIDController(5.0, 0.0, 0.0, TrapezoidProfile.Constraints(maxSpeedMetersPerSecond, maxAccelerationMetersPerSecond))

    private var finalPosition = position.applyFlip()

    override fun initialize() {
        finalPosition = position.applyFlip()
        Logger.recordOutput("Drive/PositionGoal", finalPosition)
        Drivetrain.setAngleGoal(finalPosition.rotation)

        val (xDistance, yDistance) = getDistanceToGoal()
        pid.reset(hypot(xDistance, yDistance))
    }

    override fun execute() {
        val (xDistance, yDistance) = getDistanceToGoal()
        val measurement = hypot(xDistance, yDistance)

        val result = pid.calculate(measurement, 0.0)

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
            Drivetrain.calculateAngleSpeed(),
            Gyro.getYaw()
        )

        Drivetrain.setSpeeds(speeds)
    }

    override fun isFinished(): Boolean {
        val (xDistance, yDistance) = getDistanceToGoal()
        return hypot(xDistance, yDistance) < positionalTolerance && Drivetrain.atAngleGoal(rotationalTolerance)
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