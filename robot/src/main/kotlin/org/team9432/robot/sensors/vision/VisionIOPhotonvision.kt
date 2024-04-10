package org.team9432.robot.sensors.vision

import edu.wpi.first.apriltag.AprilTagFields
import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.photonvision.PhotonCamera
import org.photonvision.targeting.PhotonPipelineResult
import org.team9432.lib.unit.Length
import org.team9432.lib.unit.meters
import org.team9432.robot.FieldConstants.onField
import org.team9432.robot.RobotPosition
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs

class VisionIOPhotonvision: VisionIO {
    private val camera = PhotonCamera("Limelight")
    private val aprilTagFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField()
    private val robotToCamera = robotToCameraArducam

    private val useMultitag = false

    override fun updateInputs(inputs: VisionIO.VisionIOInputs) {
        inputs.connected = camera.isConnected
        inputs.trackedTags = emptyArray()

        val result = camera.latestResult

        if (!result.hasTargets()) return

        val (pose, tagArea, tagsUsed) = if (!useMultitag) {
            updateNonMultitag(result)
        } else {
            updateMultitag(result) ?: updateNonMultitag(result)
        } ?: return

        val speed = Drivetrain.getFieldRelativeSpeeds()
        val poseDifference = RobotPosition.distanceTo(pose.translation)
        var xyDeviation: Length
        if (speed.vxMetersPerSecond + speed.vyMetersPerSecond <= 0.2 && tagArea > 0.4) {
            xyDeviation = 0.1.meters
        } else if (useMultitag && tagArea > 0.05) {
            xyDeviation = 0.5.meters
            if (tagArea > 0.09) {
                xyDeviation = 0.1.meters
            }
        } else if (tagArea > 0.8 /* && poseDifference < 0.5 */) {
            xyDeviation = 1.0.meters
        } else if (tagArea > 0.1 /* && poseDifference < 0.3 */) {
            xyDeviation = 2.0.meters
        } else {
            return // The tag is really far away
        }

        Drivetrain.setVisionStandardDeviations(xyDeviation)
        Drivetrain.addVisionMeasurement(pose, result.timestampSeconds)

        inputs.trackedTags = tagsUsed.mapNotNull { aprilTagFieldLayout.getTagPose(it).getOrNull() }.toTypedArray()
    }

    private fun updateMultitag(result: PhotonPipelineResult): VisionResult? {
        val multiTagResult = result.multiTagResult
        if (!multiTagResult.estimatedPose.isPresent) return null

        val cameraToField = result.multiTagResult.estimatedPose.best
        val pose = Pose3d().plus(cameraToField).relativeTo(aprilTagFieldLayout.origin).plus(robotToCamera.inverse())

        if (!isPositionValid(pose)) return null

        val tagsUsed = result.getTargets().filter { multiTagResult.fiducialIDsUsed.contains(it.fiducialId) }
        val largestArea = tagsUsed.maxBy { it.area }.area

        return VisionResult(pose.toPose2d(), largestArea, tagsUsed.map { it.fiducialId })
    }

    private fun updateNonMultitag(result: PhotonPipelineResult): VisionResult? {
        val poses = mutableListOf<VisionTarget>()
        for (target in result.targets) {
            val targetFiducialId = target.fiducialId
            val targetPosition = aprilTagFieldLayout.getTagPose(targetFiducialId).getOrNull() ?: continue
            val estimatedPose = targetPosition.transformBy(target.bestCameraToTarget.inverse()).transformBy(robotToCamera.inverse())
            poses.add(VisionTarget(targetFiducialId, estimatedPose, target.poseAmbiguity, target.area))
        }

        // Filter out any bad estimations
        val filteredTargets = poses.filter {
            isPositionValid(it.pose) && it.ambiguity < 0.075
        }

        // Record information
        filteredTargets.forEach { target -> Logger.recordOutput("Vision/Tags/${target.id}/Area", target.area) }
        filteredTargets.groupBy { it.id }.forEach { (id, targets) ->
            targets.forEachIndexed { index, visionPose ->
                Logger.recordOutput("Vision/Tags/$id/Ambiguity-$index", visionPose.ambiguity)
            }
        }

        // Take the position closest to the current robot position from each tag
        val finalTargets = mutableListOf<VisionTarget>()
        filteredTargets.groupBy { it.id }.values.forEach { tagPoses -> finalTargets.add(tagPoses.minBy { RobotPosition.distanceTo(it.pose.toPose2d().translation) }) }

        // Record these final positions
        Logger.recordOutput("Vision/AllPoses", *finalTargets.map { it.pose }.toTypedArray())

        // Return the one that's closest to where the robot already is
        val target = finalTargets.minByOrNull { RobotPosition.distanceTo(it.pose.toPose2d().translation) } ?: return null

        return VisionResult(target.pose.toPose2d(), target.area, listOf(target.id))
    }

    private fun isPositionValid(pose: Pose3d) = abs(pose.z) < 0.25 && pose.toPose2d().onField()

    data class VisionTarget(val id: Int, val pose: Pose3d, val ambiguity: Double, val area: Double)
    data class VisionResult(val pose: Pose2d, val area: Double, val usedTags: List<Int>)

    private val robotToCameraArducam
        get() = Transform3d(
            Translation3d(
                Units.inchesToMeters(1.4),
                Units.inchesToMeters(-2.5),
                Units.inchesToMeters(15.022) + 0.124460
            ),
            Rotation3d(
                0.0,
                Math.toRadians(-12.0),
                0.0
            )
        )

    private val robotToCameraLimeLight
        get() = Transform3d(
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
}