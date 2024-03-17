package org.team9432.robot.subsystems.gyro

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveModulePosition
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.drivetrain.Drivetrain.kinematics
import org.team9432.robot.subsystems.drivetrain.Drivetrain.modules

object Gyro: KSubsystem() {
    private val io: GyroIO
    private val inputs = LoggedGyroIOInputs()

    init {
        io = when (Robot.mode) {
            REAL, REPLAY -> GyroIOPigeon2()
            SIM -> object: GyroIO {}
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Gyro", inputs)
    }

    private var lastModulePositions = MutableList(4) { SwerveModulePosition() }
    private var disconnectedAngle = Rotation2d()

    fun getYaw(): Rotation2d {
        return if (inputs.connected) {
            inputs.yaw
        } else {
            val moduleDeltas = arrayOfNulls<SwerveModulePosition>(4)
            val modulePositions = Drivetrain.getModulePositions()

            for (i in modules.indices) {
                moduleDeltas[i] = SwerveModulePosition(
                    modulePositions[i].distanceMeters - lastModulePositions[i].distanceMeters,
                    modulePositions[i].angle
                )
                lastModulePositions[i] = modulePositions[i]
            }

            // Use the angle delta from the kinematics and module deltas
            val twist = kinematics.toTwist2d(*moduleDeltas)
            val newAngle = disconnectedAngle.plus(Rotation2d(twist.dtheta))
            disconnectedAngle = newAngle
            return newAngle
        }
    }

    fun resetYaw() {
        setYaw(Rotation2d())
    }

    fun setYaw(angle: Rotation2d) {
        disconnectedAngle = angle
        io.setYaw(angle.degrees)
        Drivetrain.resetAngleController(angle)
        Drivetrain.resetPosition(Drivetrain.getPose(), angle)
    }
}