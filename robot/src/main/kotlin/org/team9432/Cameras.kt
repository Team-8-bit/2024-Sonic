package org.team9432

import edu.wpi.first.cameraserver.CameraServer
import edu.wpi.first.cscore.CvSink
import edu.wpi.first.cscore.CvSource
import edu.wpi.first.cscore.UsbCamera
import org.opencv.core.Mat
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs

object Cameras : KSubsystem() {
    private var currentCamera = MechanismSide.AMP

    private val ampCam: UsbCamera = CameraServer.startAutomaticCapture(1)
    private val speakerCam: UsbCamera = CameraServer.startAutomaticCapture(0)

    private val ampSink: CvSink = CameraServer.getVideo(ampCam)
    private val speakerSink: CvSink = CameraServer.getVideo(speakerCam)
    // private val outputStream: CvSource = CameraServer.putVideo("Driver Cam", 320 / 2, 240 / 2)

    init {
        ampCam.setResolution(320 / 4, 240 / 4)
        ampCam.setFPS(8)
        speakerCam.setResolution(320 / 4, 240 / 4)
        speakerCam.setFPS(8)
    }

    private fun selectCamera(): MechanismSide {
        // Only return a new camera when the robot is actually going
        val speeds = Drivetrain.getSpeeds()

        return if (maxOf(abs(speeds.vxMetersPerSecond), abs(speeds.vyMetersPerSecond)) > 0.5) {
            RobotState.getMovementDirection()
        } else {
            currentCamera
        }
    }

    override fun periodic() {
        currentCamera = selectCamera()

        when (currentCamera) {
            MechanismSide.AMP -> {
                speakerSink.setEnabled(false)
                ampSink.setEnabled(true)
            }

            MechanismSide.SPEAKER -> {
                ampSink.setEnabled(false)
                speakerSink.setEnabled(true)
            }
        }
//
//        val image = Mat()
//
//        when (currentCamera) {
//            MechanismSide.AMP -> ampSink.grabFrame(image)
//            MechanismSide.SPEAKER -> speakerSink.grabFrame(image)
//        }
        //outputStream.putFrame(image)
    }
}