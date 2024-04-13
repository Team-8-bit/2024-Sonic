package org.team9432.robot.sensors.vision

import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.photonvision.PhotonCamera
import org.photonvision.targeting.PhotonPipelineResult
import org.team9432.lib.unit.Length
import org.team9432.lib.unit.meters
import org.team9432.robot.FieldConstants
import org.team9432.robot.FieldConstants.onField
import org.team9432.robot.RobotPosition
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs

class VisionIOPhotonvision: VisionIO {
    private val camera = PhotonCamera("Limelight")
    private val robotToCamera = robotToCameraArducam

    private val useMultitag = true

    override fun updateInputs(inputs: VisionIO.VisionIOInputs) {
        inputs.connected = camera.isConnected

        val result = camera.latestResult

        // Record information
        result.targets.forEach { target -> Logger.recordOutput("Vision/Tags/${target.fiducialId}/Area", target.area) }
        result.targets.groupBy { it.fiducialId }.forEach { (id, targets) ->
            targets.forEachIndexed { index, visionPose ->
                Logger.recordOutput("Vision/Tags/$id/Ambiguity-$index", visionPose.poseAmbiguity)
            }
        }

        val output = getVisionOutput(result)

        if (output != null) {
            val (xyDeviation, pose, tagsUsed) = output
            Drivetrain.setVisionStandardDeviations(xyDeviation)
            Drivetrain.addVisionMeasurement(pose.toPose2d(), result.timestampSeconds)
            inputs.trackedTags = tagsUsed.mapNotNull { FieldConstants.aprilTagFieldLayout.getTagPose(it).getOrNull() }.toTypedArray()
            Logger.recordOutput("Vision/EstimatedRobotPose", *arrayOf(pose))
        } else {
            inputs.trackedTags = emptyArray()
            Logger.recordOutput("Vision/EstimatedRobotPose", emptyArray())
        }
    }

    private fun getVisionOutput(result: PhotonPipelineResult): VisionOutput? {
        if (!result.hasTargets()) return null

        val (pose, tagArea, tagsUsed) = if (!useMultitag) {
            updateNonMultitag(result)
        } else {
            updateMultitag(result) ?: updateNonMultitag(result)
        } ?: return null

        val speed = Drivetrain.getFieldRelativeSpeeds()

        val xyDeviation = if (speed.vxMetersPerSecond + speed.vyMetersPerSecond <= 0.2 && tagArea > 0.3) {
            0.05.meters
        } else if (tagArea < 0.15) {
            2.0.meters
        } else if (tagArea < 0.4) {
            1.0.meters
        } else {
            return null // The tag is really far away
        }

        return VisionOutput(xyDeviation, pose, tagsUsed)
    }

    private fun updateMultitag(result: PhotonPipelineResult): VisionResult? {
        val multiTagResult = result.multiTagResult
        if (!multiTagResult.estimatedPose.isPresent) return null

        val cameraToField = result.multiTagResult.estimatedPose.best
        val pose = Pose3d().plus(cameraToField).relativeTo(FieldConstants.aprilTagFieldLayout.origin).plus(robotToCamera.inverse())

        if (!isPositionValid(pose)) return null

        val tagsUsed = result.getTargets().filter { multiTagResult.fiducialIDsUsed.contains(it.fiducialId) }
        val largestArea = tagsUsed.maxBy { it.area }.area

        return VisionResult(pose, largestArea, tagsUsed.map { it.fiducialId })
    }

    private fun updateNonMultitag(result: PhotonPipelineResult): VisionResult? {
        val poses = mutableListOf<VisionTarget>()
        for (target in result.targets) {
            val targetFiducialId = target.fiducialId
            val targetPosition = FieldConstants.aprilTagFieldLayout.getTagPose(targetFiducialId).getOrNull() ?: continue
            val estimatedPose = targetPosition.transformBy(target.bestCameraToTarget.inverse()).transformBy(robotToCamera.inverse())
            poses.add(VisionTarget(targetFiducialId, estimatedPose, target.poseAmbiguity, target.area))
        }

        // Filter out any bad estimations
        val filteredTargets = poses.filter {
            isPositionValid(it.pose) && it.ambiguity < 0.1
        }

        // Take the position closest to the current robot position from each tag
        val finalTargets = mutableListOf<VisionTarget>()
        filteredTargets.groupBy { it.id }.values.forEach { tagPoses -> finalTargets.add(tagPoses.minBy { RobotPosition.distanceTo(it.pose.toPose2d().translation) }) }

        // Record these final positions
        Logger.recordOutput("Vision/AllPoses", *finalTargets.map { it.pose }.toTypedArray())

        // Return the one that's closest to where the robot already is
        val target = finalTargets.minByOrNull { RobotPosition.distanceTo(it.pose.toPose2d().translation) } ?: return null

        return VisionResult(target.pose, target.area, listOf(target.id))
    }

    private fun isPositionValid(pose: Pose3d) = abs(pose.z) < 0.25 && pose.toPose2d().onField()

    data class VisionTarget(val id: Int, val pose: Pose3d, val ambiguity: Double, val area: Double)
    data class VisionResult(val pose: Pose3d, val area: Double, val usedTags: List<Int>)
    data class VisionOutput(val visionStandardDeviation: Length, val pose: Pose3d, val tagsUsed: List<Int>)

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