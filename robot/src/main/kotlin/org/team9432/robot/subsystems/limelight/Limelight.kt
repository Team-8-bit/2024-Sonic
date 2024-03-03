package org.team9432.robot.subsystems.limelight

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.geometry.Translation3d
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object Limelight: KSubsystem() {
    private val io: LimelightIO
    private val inputs = LoggedLimelightIOInputs()

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = LimelightIONeo()
                io.setPID(0.0, 0.0, 0.0)
            }

            SIM -> {
                io = object: LimelightIO {}
                io.setPID(0.0, 0.0, 0.0)
            }
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Limelight", inputs)
    }

    fun setAngle(angle: Rotation2d) {
        io.setAngle(angle)

        Logger.recordOutput("Limelight/AngleSetpointDegrees", angle.degrees)
    }

    fun stop() = io.stop()

    fun getCurrentRobotToCamera(): Transform3d {
        val limelightRotationTransform = Transform3d(Translation3d(), Rotation3d(0.0, 0.0, inputs.absoluteAngle.radians))

        // TODO get the actual base transformation
        return Transform3d().plus(limelightRotationTransform)
    }
}