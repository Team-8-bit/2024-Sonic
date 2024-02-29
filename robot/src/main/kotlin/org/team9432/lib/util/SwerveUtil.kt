package org.team9432.lib.util

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.SwerveModuleState
import org.team9432.lib.wpilib.ChassisSpeeds
import kotlin.math.abs
import kotlin.math.cos

object SwerveUtil {
    // Full credit to team 95 for this method of reducing translational skew https://github.com/first95/FRC2024/blob/3e069410f2c6c7a8966ea9c792ac04b007a731ef/2024_robot/src/main/java/frc/robot/subsystems/SwerveBase.java#L400
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

    // Custom optimize method by team 364
    fun optimize(desiredState: SwerveModuleState, currentAngle: Double): SwerveModuleState {
        var targetAngle = placeInAppropriate0To360Scope(currentAngle, desiredState.angle.degrees)
        var targetSpeed = desiredState.speedMetersPerSecond
        val delta = targetAngle - currentAngle
        if (abs(delta) > 90) {
            targetSpeed = -targetSpeed
            if (delta > 90) {
                targetAngle -= 180.0
            } else {
                targetAngle += 180.0
            }
        }
        return SwerveModuleState(targetSpeed, Rotation2d.fromDegrees(targetAngle))
    }

    private fun placeInAppropriate0To360Scope(scopeReference: Double, newAngle: Double): Double {
        var outputAngle = newAngle
        val lowerBound: Double
        val upperBound: Double
        val lowerOffset = scopeReference % 360
        if (lowerOffset >= 0) {
            lowerBound = scopeReference - lowerOffset
            upperBound = scopeReference + (360 - lowerOffset)
        } else {
            upperBound = scopeReference - lowerOffset
            lowerBound = scopeReference - (360 + lowerOffset)
        }
        while (outputAngle < lowerBound) {
            outputAngle += 360.0
        }
        while (outputAngle > upperBound) {
            outputAngle -= 360.0
        }
        if (outputAngle - scopeReference > 180) {
            outputAngle -= 360.0
        } else if (outputAngle - scopeReference < -180) {
            outputAngle += 360.0
        }
        return outputAngle
    }
}