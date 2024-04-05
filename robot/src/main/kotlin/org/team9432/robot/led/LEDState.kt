package org.team9432.robot.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.DriverStation.Alliance
import org.team9432.lib.State
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.robot.RobotState
import org.team9432.robot.led.animations.ParallelAnimationGroup
import org.team9432.robot.led.animations.predefined.layered.ColorShift
import org.team9432.robot.led.animations.predefined.simple.Pulse
import org.team9432.robot.led.animations.predefined.simple.Solid
import org.team9432.robot.led.animations.predefined.simple.Strobe
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.presets.*
import org.team9432.robot.oi.EmergencySwitches
import org.team9432.robot.sensors.vision.Vision

object LEDState: KPeriodic() {
    private val animationScope = AnimationBindScope.build {
        If({ testEmergencySwitchActive }) {
            addAnimation(
                ParallelAnimationGroup(
                    Solid(LEDs.Section.SPEAKER_LEFT, Color.Green),
                    Solid(LEDs.Section.SPEAKER_RIGHT, Color.Red),
                    Solid(LEDs.Section.AMP_LEFT, Color.Blue),
                    Solid(LEDs.Section.AMP_RIGHT, Color.Yellow),
                    Solid(LEDs.Section.TOP_BAR, Color.White)
                )
            )
        }.ElseIf({ driverstationDisabled }) {
            addAnimation(
                ParallelAnimationGroup(
                    Pulse(LEDs.Section.SPEAKER_LEFT, Color.White, 2.0, 1.0),
                    Pulse(LEDs.Section.SPEAKER_RIGHT, Color.White, 2.0, 1.0),
                    Pulse(LEDs.Section.AMP_LEFT, Color.White, 2.0, 1.0),
                    Pulse(LEDs.Section.AMP_RIGHT, Color.White, 2.0, 1.0),
                )
            )

            If({ alliance == null }) {
                addAnimation(ColorShift(LEDs.Section.TOP_RIGHT, listOf(Color.Black, Color.White)))
            }.ElseIf({ alliance == Alliance.Red }) {
                addAnimation(Solid(LEDs.Section.TOP_RIGHT, Color.Red))
            }.ElseIf({ alliance == Alliance.Blue }) {
                addAnimation(Solid(LEDs.Section.TOP_RIGHT, Color.Blue))
            }

            If({ limelightNotConnected }) {
                addAnimation(Strobe(LEDs.Section.TOP_LEFT, Color.Red, 0.5))
            }.ElseIf({ hasVisionTarget }) {
                addAnimation(Solid(LEDs.Section.TOP_LEFT, Color.LimeGreen))
            }
        }.ElseIf({ driverstationAutonomous }) {
            addAnimation(Strobe(LEDs.Section.ALL, Color.Red, 0.25))
        }.ElseIf({ driverstationTeleop }) {
            // I think this will keep rainbowing even if there are other animations running
            addAnimation(ColorShift(LEDs.Section.ALL, Color.RainbowColors, 1.0, 10))

            If({ speakerShooterReady }) {
                addAnimation(Strobe(LEDs.Section.SPEAKER, Color.Lime, 0.25))
            }.ElseIf({ ampShooterReady }) {
                If({ alliance == Alliance.Red }) {
                    addAnimation(Strobe(LEDs.Section.LEFT, Color.Lime, 0.25))
                }.Else {
                    addAnimation(Strobe(LEDs.Section.RIGHT, Color.Lime, 0.25))
                }
            }.ElseIf({ noteInIntake }) {
                addAnimation(Strobe(LEDs.Section.ALL, Color.Purple, 0.1))
            }
        }
    }

    var alliance: Alliance? = null

    var noteInIntake = false
    var hasVisionTarget = false
    var limelightNotConnected = false
    var testEmergencySwitchActive = false

    var speakerShooterReady = false
    var ampShooterReady = false

    var driverstationDisabled = false
    var driverstationAutonomous = false
    var driverstationTeleop = false

    override fun periodic() {
        noteInIntake = RobotState.notePosition.isIntake
        hasVisionTarget = Vision.hasVisionTarget()
        limelightNotConnected = !Vision.connected
        testEmergencySwitchActive = EmergencySwitches.testSwitchActive
        driverstationDisabled = DriverStation.isDisabled()
        driverstationAutonomous = DriverStation.isAutonomousEnabled()
        driverstationTeleop = DriverStation.isTeleopEnabled()
        alliance = State.alliance

        animationScope.update()
    }
}