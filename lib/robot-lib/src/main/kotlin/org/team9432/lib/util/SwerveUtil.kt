package org.team9432.lib.util

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.util.Units
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot

object SwerveUtil {
    /**
     * Takes in a [ChassisSpeeds] and modifies it to reduce the x/y drift while the robot is spinning.
     *
     * Full credit to team 95 for this method of reducing skew https://github.com/first95/FRC2024/blob/3e069410f2c6c7a8966ea9c792ac04b007a731ef/2024_robot/src/main/java/frc/robot/subsystems/SwerveBase.java#L400
     */
    fun correctForDynamics(initial: ChassisSpeeds, dt: Double, magicFactor: Double = 6.0): ChassisSpeeds {
        val oneMinusCos = 1 - cos(initial.omegaRadiansPerSecond * dt)
        return if (abs(oneMinusCos) < 1E-9) {
            initial
        } else {
            val linearVel = Translation2d(initial.vxMetersPerSecond, initial.vyMetersPerSecond)
            val tangentVel = linearVel.norm
            val radius = tangentVel / initial.omegaRadiansPerSecond
            val skewVelocity = radius * oneMinusCos / dt
            val direction = linearVel.angle.minus(Rotation2d.fromDegrees(90.0))
            val velocityCorrection = Translation2d(skewVelocity, direction).times(magicFactor)
            val translationVel = linearVel.plus(velocityCorrection)
            ChassisSpeeds(
                translationVel.x,
                translationVel.y,
                initial.omegaRadiansPerSecond
            )
        }
    }

    /** Returns an array of [Translation2d] of Mk4i swerve module positions given a frame size. Useful for WPILib odometry classes. */
    fun getMk4iModuleTranslations(frameSizeInches: Double): Array<Translation2d> {
        val distanceFromCenterXY = (frameSizeInches / 2) - 2.625
        val distanceInches = hypot(distanceFromCenterXY, distanceFromCenterXY)
        val moduleDistance = Units.inchesToMeters(distanceInches)

        val frontLeft = Translation2d(moduleDistance, moduleDistance)
        val frontRight = Translation2d(moduleDistance, -moduleDistance)
        val backLeft = Translation2d(-moduleDistance, moduleDistance)
        val backRight = Translation2d(-moduleDistance, -moduleDistance)
        return arrayOf(frontLeft, frontRight, backLeft, backRight)
    }
}