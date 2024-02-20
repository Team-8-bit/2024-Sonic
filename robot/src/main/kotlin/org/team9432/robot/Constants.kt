package org.team9432.robot

import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.util.Units
import org.team9432.robot.subsystems.drivetrain.ModuleIO
import kotlin.math.pow

@Suppress("unused")
object DrivetrainConstants {
    const val MK4I_L2_DRIVE_REDUCTION = 6.75
    const val MK4I_L2_STEER_REDUCTION = 21.428571428571427

    const val DRIVE_WHEEL_DIAMETER = 4.0
    const val DRIVE_WHEEL_CIRCUMFERENCE = DRIVE_WHEEL_DIAMETER * kotlin.math.PI

    val MODULE_TRANSLATIONS: Array<Translation2d>
        get() {
            val moduleDistance = Units.inchesToMeters(SWERVE_MODULE_DISTANCE_FROM_CENTER)
            val frontLeft = Translation2d(moduleDistance, moduleDistance)
            val frontRight = Translation2d(moduleDistance, -moduleDistance)
            val backLeft = Translation2d(-moduleDistance, moduleDistance)
            val backRight = Translation2d(-moduleDistance, -moduleDistance)
            return arrayOf(frontLeft, frontRight, backLeft, backRight)
        }

    const val SWERVE_MODULE_DISTANCE_FROM_CENTER = 15.25357313346778

    val MODULE_OFFSETS = mapOf(
        ModuleIO.Module.FL to 190.72265625,
        ModuleIO.Module.FR to 288.9843658447266,
        ModuleIO.Module.BL to 71.45507469177247,
        ModuleIO.Module.BR to 306.73826751708987
    )

    const val ROBOT_WIDTH = 27 + 6 // Robot width with bumpers, in inches

    const val MAX_ANGULAR_SPEED_DEGREES_PER_SECOND = 360.0
    val MAX_ANGULAR_SPEED_DEGREES_PER_SECOND_SQUARED = MAX_ANGULAR_SPEED_DEGREES_PER_SECOND.pow(2)

    const val MAX_VELOCITY_METERS_PER_SECOND = 5.0
    const val MAX_ACCELERATION_METERS_PER_SECOND_SQUARED = 20.0

    object AngleConstants {
        const val P = 5.0
        const val I = 0.0
        const val D = 0.0
        const val EPSILON = 0.0

        // Constraints for the profiled angle controller
        val CONTROLLER_CONSTRAINTS = TrapezoidProfile.Constraints(MAX_ANGULAR_SPEED_DEGREES_PER_SECOND, MAX_ANGULAR_SPEED_DEGREES_PER_SECOND_SQUARED)
    }

    object PoseConstants {
        const val P = 0.0
        const val I = 0.0
        const val D = 0.0
        const val EPSILON = 0.0
    }
}