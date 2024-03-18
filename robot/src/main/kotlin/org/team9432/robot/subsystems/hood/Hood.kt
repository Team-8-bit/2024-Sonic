package org.team9432.robot.subsystems.hood

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.oi.EmergencySwitches

object Hood: KSubsystem() {
    private val io: HoodIO
    private val inputs = LoggedHoodIOInputs()

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = HoodIONeo()
                io.setPID(2.25, 0.0, 0.0)
            }

            SIM -> {
                io = HoodIOSim()
                io.setPID(1.0, 0.0, 0.0)
            }
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Hood", inputs)

        Logger.recordOutput("Subsystems/Hood", Pose3d(Translation3d(0.266700, 0.0, 0.209550 + 0.124460), Rotation3d(0.0, inputs.absoluteAngle.radians, 0.0)))

        if (EmergencySwitches.isSubwooferOnly) {
            io.stop()
        }
    }

    fun setAngle(angle: Rotation2d) {
        if (!EmergencySwitches.isSubwooferOnly) {
            io.setAngle(Rotation2d.fromDegrees(MathUtil.clamp(angle.degrees, 0.0, 30.0)))
        }

        Logger.recordOutput("Hood/AngleSetpointDegrees", angle.degrees)
    }

    fun stop() = io.stop()
}