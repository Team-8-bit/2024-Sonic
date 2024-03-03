package org.team9432.robot.subsystems.vision

import edu.wpi.first.math.geometry.Pose2d
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object Vision: KSubsystem() {
    private val io: VisionIO
    private val inputs = LoggedVisionIOInputs()

    init {
        io = when (Robot.mode) {
            REAL, REPLAY -> VisionIOPhotonvision
            SIM -> object: VisionIO {}
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Vision", inputs)
    }

    fun getEstimatedPose2d(): Pair<Pose2d, Double>? {
        val pose = inputs.estimatedRobotPose
        val timestamp = inputs.poseTimestamp
        return if (pose.isNotEmpty() && timestamp.isNotEmpty()) pose.first().toPose2d() to timestamp.first()
        else null
    }
}