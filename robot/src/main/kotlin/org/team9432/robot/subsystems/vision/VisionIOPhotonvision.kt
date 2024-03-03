package org.team9432.robot.subsystems.vision

import edu.wpi.first.apriltag.AprilTagFields
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import org.photonvision.PhotonCamera
import org.photonvision.PhotonPoseEstimator
import org.photonvision.targeting.PhotonTrackedTarget
import org.team9432.robot.subsystems.limelight.Limelight
import kotlin.jvm.optionals.getOrNull

object VisionIOPhotonvision: VisionIO {
    private val camera = PhotonCamera("Limelight")
    private val aprilTagFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField()
    private val photonPoseEstimator = PhotonPoseEstimator(
        aprilTagFieldLayout,
        PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
        camera,
        Limelight.getCurrentRobotToCamera()
    )

    override fun updateInputs(inputs: VisionIO.VisionIOInputs) {
        val result = camera.latestResult

        if (result.hasTargets()) {
            val targets = result.targets

            inputs.allCorners = targets.getCornerArray()
            inputs.trackedTags = targets.map { it.fiducialId }.toIntArray()
        } else {
            inputs.allCorners = emptyArray()
            inputs.trackedTags = intArrayOf()
        }

        photonPoseEstimator.robotToCameraTransform = Limelight.getCurrentRobotToCamera()
        val estimatedPose = photonPoseEstimator.update().getOrNull()

        inputs.usedCorners = estimatedPose?.targetsUsed?.getCornerArray() ?: emptyArray()

        inputs.poseTimestamp = estimatedPose?.timestampSeconds?.let { doubleArrayOf(it) } ?: doubleArrayOf()
        inputs.estimatedRobotPose = estimatedPose?.estimatedPose?.let { arrayOf(it) } ?: emptyArray()
    }
    private fun List<PhotonTrackedTarget>.getCornerArray() = this.flatMap { t -> t.detectedCorners.map { Pose2d(it.x, it.y, Rotation2d()) } }.toTypedArray()
}