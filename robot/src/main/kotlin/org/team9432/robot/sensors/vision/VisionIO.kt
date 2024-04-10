package org.team9432.robot.sensors.vision

import edu.wpi.first.math.geometry.Pose3d
import org.team9432.lib.annotation.Logged

interface VisionIO {
    @Logged
    open class VisionIOInputs {
        var trackedTags = emptyArray<Pose3d>()
        var connected = false
    }

    fun updateInputs(inputs: VisionIOInputs) {}
}