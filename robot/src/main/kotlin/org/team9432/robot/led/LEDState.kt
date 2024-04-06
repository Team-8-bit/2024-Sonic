package org.team9432.robot.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.DriverStation.Alliance
import org.team9432.lib.State
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.robot.RobotState
import org.team9432.robot.led.animation.AnimationBindScope
import org.team9432.robot.led.animation.groups.ParallelAnimationGroup
import org.team9432.robot.led.animation.layered.ColorShift
import org.team9432.robot.led.animation.simple.Pulse
import org.team9432.robot.led.animation.simple.Solid
import org.team9432.robot.led.animation.simple.Strobe
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.predefined.*
import org.team9432.robot.led.strip.Sections
import org.team9432.robot.oi.EmergencySwitches
import org.team9432.robot.sensors.vision.Vision

object LEDState: KPeriodic() {
    private val animationScope = AnimationBindScope.build {
        If({ testEmergencySwitchActive }) {
            addAnimation(
                ParallelAnimationGroup(
                    Solid(Sections.SPEAKER_LEFT, Color.Green),
                    Solid(Sections.SPEAKER_RIGHT, Color.Red),
                    Solid(Sections.AMP_LEFT, Color.Blue),
                    Solid(Sections.AMP_RIGHT, Color.Yellow),
                    Solid(Sections.TOP_BAR, Color.White)
                )
            )
        }.ElseIf({ driverstationDisabled }) {
            addAnimation(
                ParallelAnimationGroup(
                    Pulse(Sections.SPEAKER_LEFT, Color.White, 2.0, 1.0),
                    Pulse(Sections.SPEAKER_RIGHT, Color.White, 2.0, 1.0),
                    Pulse(Sections.AMP_LEFT, Color.White, 2.0, 1.0),
                    Pulse(Sections.AMP_RIGHT, Color.White, 2.0, 1.0),
                )
            )

            If({ alliance == null }) {
                addAnimation(ColorShift(Sections.TOP_RIGHT, listOf(Color.Black, Color.White)))
            }.ElseIf({ alliance == Alliance.Red }) {
                addAnimation(Solid(Sections.TOP_RIGHT, Color.Red))
            }.ElseIf({ alliance == Alliance.Blue }) {
                addAnimation(Solid(Sections.TOP_RIGHT, Color.Blue))
            }

            If({ limelightNotConnected }) {
                addAnimation(Strobe(Sections.TOP_LEFT, Color.Red, 0.5))
            }.ElseIf({ hasVisionTarget }) {
                addAnimation(Solid(Sections.TOP_LEFT, Color.LimeGreen))
            }
        }.ElseIf({ driverstationAutonomous }) {
            addAnimation(Strobe(Sections.ALL, Color.Red, 0.25))
        }.ElseIf({ driverstationTeleop }) {
            // I think this will keep rainbowing even if there are other animations running
            addAnimation(ColorShift(Sections.ALL, Color.RainbowColors, 1.0, 10))

            If({ speakerShooterReady }) {
                addAnimation(Strobe(Sections.SPEAKER, Color.Lime, 0.25))
            }.ElseIf({ ampShooterReady }) {
                If({ alliance == Alliance.Red }) {
                    addAnimation(Strobe(Sections.LEFT, Color.Lime, 0.25))
                }.Else {
                    addAnimation(Strobe(Sections.RIGHT, Color.Lime, 0.25))
                }
            }.ElseIf({ noteInIntake }) {
                addAnimation(Strobe(Sections.ALL, Color.Purple, 0.1))
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