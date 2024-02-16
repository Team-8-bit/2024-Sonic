package org.team9432.lib.util

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveModuleState
import kotlin.math.abs

object SwerveUtil {
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