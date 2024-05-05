package org.team9432.lib.constants

/**
 * Contains various swerve constants that are applicable between robots.
 */
@Suppress("unused")
object SwerveConstants {
    const val MK4I_L1_DRIVE_REDUCTION = 8.14
    const val MK4I_L2_DRIVE_REDUCTION = 6.75
    const val MK4I_L3_DRIVE_REDUCTION = 6.12
    const val MK4I_STEER_REDUCTION = 21.43

    // TODO: Find a way to calculate this on a per-robot basis
    const val MK4I_DRIVE_WHEEL_RADIUS = 2.0
    const val MK4I_DRIVE_WHEEL_CIRCUMFERENCE = MK4I_DRIVE_WHEEL_RADIUS * 2.0 * kotlin.math.PI
}