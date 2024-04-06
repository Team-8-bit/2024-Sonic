package org.team9432.robot.sensors.vision

import edu.wpi.first.apriltag.AprilTagFields
import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.photonvision.PhotonCamera
import org.photonvision.PhotonPoseEstimator
import org.photonvision.common.hardware.VisionLEDMode
import org.photonvision.targeting.PhotonTrackedTarget
import java.util.*
import kotlin.jvm.optionals.getOrNull

class VisionIOPhotonvision: VisionIO {
    private val robotToCameraArducam = Transform3d(
        Translation3d(
            Units.inchesToMeters(1.56),
            Units.inchesToMeters(-2.5),
            Units.inchesToMeters(15.064) + 0.124460
        ),
        Rotation3d(
            0.0,
            Math.toRadians(-15.0),
            0.0
        )
    )

    private val robotToCameraLimeLight = Transform3d(
        Translation3d(
            Units.inchesToMeters(1.342924),
            Units.inchesToMeters(0.0),
            Units.inchesToMeters(17.048946) + 0.124460
        ),
        Rotation3d(
            0.0,
            Math.toRadians(-20.0),
            0.0
        )
    )

    private val camera = PhotonCamera("Limelight")
    private val aprilTagFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField()
    private val photonPoseEstimator = PhotonPoseEstimator(
        aprilTagFieldLayout,
        PhotonPoseEstimator.PoseStrategy.LOWEST_AMBIGUITY,
//        PhotonPoseEstimator.PoseStrategy.CLOSEST_TO_LAST_POSE,
        camera,
        robotToCameraArducam
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

        val poses = mutableListOf<Pose3d>()
//        photonPoseEstimator.setLastPose(Drivetrain.getPose())
        for (target in result.targets) {
            val targetFiducialId = target.fiducialId
            val targetPosition = aprilTagFieldLayout.getTagPose(targetFiducialId).getOrNull() ?: continue
            val estimatedPose = targetPosition.transformBy(target.bestCameraToTarget.inverse()).transformBy(robotToCameraArducam.inverse())
            poses.add(estimatedPose)
        }

        Logger.recordOutput("Vision/AllPoses", *poses.toTypedArray())

        val estimatedPose = photonPoseEstimator.update().getOrNull()

        inputs.usedCorners = estimatedPose?.targetsUsed?.getCornerArray() ?: emptyArray()
        inputs.poseTimestamp = estimatedPose?.timestampSeconds?.let { doubleArrayOf(it) } ?: doubleArrayOf()
        inputs.estimatedRobotPose = estimatedPose?.estimatedPose?.let { arrayOf(it) } ?: emptyArray()
        inputs.connected = camera.isConnected
    }

    private fun List<PhotonTrackedTarget>.getCornerArray() =
        this.flatMap { t -> t.detectedCorners.map { Pose2d(it.x, it.y, Rotation2d()) } }.toTypedArray()

    override fun setLED(enable: Boolean) {
        camera.setLED(if (enable) VisionLEDMode.kOn else VisionLEDMode.kOff)
    }
}