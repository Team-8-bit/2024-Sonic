package org.team9432

import edu.wpi.first.cameraserver.CameraServer
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs

// https://www.chiefdelphi.com/t/switchable-camera/458565/5
object Cameras {
    init {
        val thread = Thread {
            var currentCamera = MechanismSide.AMP

            val ampCam = CameraServer.startAutomaticCapture(1)
            val speakerCam = CameraServer.startAutomaticCapture(0)

            val server = CameraServer.getServer()

            fun selectCamera(): MechanismSide {
                // Only return a new camera when the robot is actually going
                val speeds = Drivetrain.getSpeeds()

                return if (maxOf(abs(speeds.vxMetersPerSecond), abs(speeds.vyMetersPerSecond)) > 0.5) {
                    RobotState.getMovementDirection()
                } else {
                    currentCamera
                }
            }

            server.source = ampCam

            var lastCamera = currentCamera

            while (!Thread.interrupted()) {
                currentCamera = selectCamera()

                if (currentCamera != lastCamera) {
                    lastCamera = currentCamera

                    // Disable and wait
                    server.source = null
                    Thread.sleep(500)

                    // Set the new camera
                    when (currentCamera) {
                        MechanismSide.AMP -> server.source = ampCam
                        MechanismSide.SPEAKER -> server.source = speakerCam
                    }
                }
                Thread.sleep(20)
            }
        }

        thread.start()
    }
}