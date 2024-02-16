package org.team9432.lib.wpilib

import edu.wpi.first.math.geometry.Rotation2d
import kotlin.math.cos
import kotlin.math.sin

// Removes pesky rotation2d requirements
class ChassisSpeeds(vxMetersPerSecond: Double = 0.0, vyMetersPerSecond: Double = 0.0, omegaRadiansPerSecond: Double = 0.0): edu.wpi.first.math.kinematics.ChassisSpeeds(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond) {
    companion object {
        fun fromFieldRelativeSpeeds(
            vxMetersPerSecond: Double,
            vyMetersPerSecond: Double,
            omegaRadiansPerSecond: Double,
            robotAngle: Double,
        ): ChassisSpeeds {
            val angleInRadians = Math.toRadians(robotAngle)
            val vxRobot = vxMetersPerSecond * cos(angleInRadians) + vyMetersPerSecond * sin(angleInRadians)
            val vyRobot = -vxMetersPerSecond * sin(angleInRadians) + vyMetersPerSecond * cos(angleInRadians)
            return ChassisSpeeds(vxRobot, vyRobot, omegaRadiansPerSecond)
        }

        fun fromFieldRelativeSpeeds(
            fieldRelativeSpeeds: edu.wpi.first.math.kinematics.ChassisSpeeds,
            robotAngle: Double,
        ): edu.wpi.first.math.kinematics.ChassisSpeeds {
            return fromFieldRelativeSpeeds(
                fieldRelativeSpeeds.vxMetersPerSecond,
                fieldRelativeSpeeds.vyMetersPerSecond,
                fieldRelativeSpeeds.omegaRadiansPerSecond,
                robotAngle
            )
        }

        fun toFieldRelativeSpeeds(
            vxMetersPerSecond: Double,
            vyMetersPerSecond: Double,
            omegaRadiansPerSecond: Double,
            robotAngle: Rotation2d,
        ): ChassisSpeeds {
            return ChassisSpeeds(
                vxMetersPerSecond * robotAngle.cos - vyMetersPerSecond * robotAngle.sin,
                vxMetersPerSecond * robotAngle.sin + vyMetersPerSecond * robotAngle.cos,
                omegaRadiansPerSecond
            )
        }

        fun toFieldRelativeSpeeds(fieldRelativeSpeeds: ChassisSpeeds, robotAngle: Rotation2d): ChassisSpeeds {
            return toFieldRelativeSpeeds(
                fieldRelativeSpeeds.vxMetersPerSecond,
                fieldRelativeSpeeds.vyMetersPerSecond,
                fieldRelativeSpeeds.omegaRadiansPerSecond,
                robotAngle
            )
        }

        fun toFieldRelativeSpeeds(fieldRelativeSpeeds: edu.wpi.first.math.kinematics.ChassisSpeeds, robotAngle: Rotation2d): ChassisSpeeds {
            return toFieldRelativeSpeeds(
                fieldRelativeSpeeds.vxMetersPerSecond,
                fieldRelativeSpeeds.vyMetersPerSecond,
                fieldRelativeSpeeds.omegaRadiansPerSecond,
                robotAngle
            )
        }

        fun toFieldRelativeSpeeds(fieldRelativeSpeeds: ChassisSpeeds, robotAngleDegrees: Double): ChassisSpeeds {
            return toFieldRelativeSpeeds(fieldRelativeSpeeds, Rotation2d.fromDegrees(robotAngleDegrees))
        }

        fun fromWPIChassisSpeeds(speeds: edu.wpi.first.math.kinematics.ChassisSpeeds): ChassisSpeeds {
            return ChassisSpeeds(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, speeds.omegaRadiansPerSecond)
        }
    }
}
