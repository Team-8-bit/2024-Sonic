package org.team9432.robot.sensors.vision

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.net.PortForwarder
import org.littletonrobotics.junction.Logger
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object Vision: KSubsystem() {
    private val io: VisionIO
    private val inputs = LoggedVisionIOInputs()

    init {
        io = when (State.mode) {
            REAL, REPLAY -> VisionIOPhotonvision()
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

    fun hasVisionTarget() = inputs.trackedTags.isNotEmpty()

    val connected get() = inputs.connected

    fun forwardPorts() {
        PortForwarder.add(5800, "photonvision.local", 5800)
    }
}