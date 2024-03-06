package org.team9432.robot.subsystems.vision

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Pose3d
import org.team9432.lib.annotation.Logged

interface VisionIO {
    @Logged
    open class VisionIOInputs {
        var allCorners = emptyArray<Pose2d>()
        var usedCorners = emptyArray<Pose2d>()
        var trackedTags = intArrayOf()
        // these two are only one value, but wrapped in an array so it can be null/empty
        var poseTimestamp = doubleArrayOf()
        var estimatedRobotPose = emptyArray<Pose3d>()
    }

    fun updateInputs(inputs: VisionIOInputs) {}
    fun setLED(enable: Boolean) {}
}