package org.team9432.robot.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.DriverStation.Alliance
import org.team9432.lib.State
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.robot.RobotPosition
import org.team9432.robot.RobotState
import org.team9432.robot.led.animation.AnimationBindScope
import org.team9432.robot.led.animation.groups.ParallelAnimationGroup
import org.team9432.robot.led.animation.groups.SequentialAnimationGroup
import org.team9432.robot.led.animation.groups.WaitAnimation
import org.team9432.robot.led.animation.groups.repeat
import org.team9432.robot.led.animation.layered.ColorShift
import org.team9432.robot.led.animation.simple.*
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.predefined.*
import org.team9432.robot.led.strip.Sections
import org.team9432.robot.oi.switches.DSSwitches
import org.team9432.robot.sensors.vision.Vision

object LEDState: KPeriodic() {
    private val animationScope = AnimationBindScope.build {
        If({ testEmergencySwitchActive }) {
            addAnimation(
                ParallelAnimationGroup(
                    Sections.SPEAKER_LEFT.Solid(Color.Green),
                    Sections.SPEAKER_RIGHT.Solid(Color.Red),
                    Sections.AMP_LEFT.Solid(Color.Blue),
                    Sections.AMP_RIGHT.Solid(Color.Yellow),
                    Sections.TOP_BAR.Solid(Color.White)
                )
            )
        }.ElseIf({ driverstationDisabled }) {
            addAnimation(
                SequentialAnimationGroup(
                    ParallelAnimationGroup(
                        Sections.SPEAKER_LEFT.Solid(Color.Black),
                        Sections.SPEAKER_RIGHT.Solid(Color.Black),
                        Sections.AMP_LEFT.Solid(Color.Black),
                        Sections.AMP_RIGHT.Solid(Color.Black)
                    ),
                    ParallelAnimationGroup(
                        Sections.SPEAKER_LEFT.BounceToColor(Color.White, runReversed = true),
                        Sections.SPEAKER_RIGHT.BounceToColor(Color.White),
                        Sections.AMP_LEFT.BounceToColor(Color.White, runReversed = true),
                        Sections.AMP_RIGHT.BounceToColor(Color.White),
                    ),
                    ParallelAnimationGroup(
                        Sections.SPEAKER_LEFT.FadeToColor(Color.Black, 3.0, 5),
                        Sections.SPEAKER_RIGHT.FadeToColor(Color.Black, 3.0, 5),
                        Sections.AMP_LEFT.FadeToColor(Color.Black, 3.0, 5),
                        Sections.AMP_RIGHT.FadeToColor(Color.Black, 3.0, 5),
                    ),
                    WaitAnimation(1.0),
                    ParallelAnimationGroup(
                        Sections.SPEAKER_LEFT.Pulse(Color.White, 2.0, 1.0),
                        Sections.SPEAKER_RIGHT.Pulse(Color.White, 2.0, 1.0),
                        Sections.AMP_LEFT.Pulse(Color.White, 2.0, 1.0),
                        Sections.AMP_RIGHT.Pulse(Color.White, 2.0, 1.0),
                    )
                )
            )

            If({ hasVisionTarget }) {
                If({ alliance == null }) {
                    addAnimation(ColorShift(Sections.TOP_BAR, listOf(Color.Black, Color.White)))
                }.ElseIf({ alliance == Alliance.Red }) {
                    addAnimation(Sections.TOP_BAR.Solid(Color.Red))
                }.ElseIf({ alliance == Alliance.Blue }) {
                    addAnimation(Sections.TOP_BAR.Solid(Color.Blue))
                }
            }.Else {
                If({ limelightNotConnected }) {
                    addAnimation(
                        SequentialAnimationGroup(
                            Sections.TOP_BAR.Solid(Color.Black),
                            SequentialAnimationGroup(
                                Sections.TOP_BAR.BounceToColor(Color.Red),
                                Sections.TOP_BAR.BounceToColor(Color.Black)
                            ).repeat()
                        )
                    )
                }.Else {
                    addAnimation(
                        SequentialAnimationGroup(
                            Sections.TOP_BAR.Solid(Color.Black),
                            Sections.TOP_BAR.SlideToColor(Color.Green)
                        )
                    )
                }
            }
        }.ElseIf({ driverstationAutonomous }) {
            addAnimation(Sections.ALL.Strobe(Color.Red, 0.25))
        }.ElseIf({ driverstationTeleop }) {
            // I think this will keep rainbowing even if there are other animations running
            addAnimation(ColorShift(Sections.ALL, Color.RainbowColors, 1.0, 10))

            If({ inSpeakerRange }) {
                addAnimation(Sections.SPEAKER.Strobe(Color.White, 0.5))
            }.ElseIf({ noteInIntake }) {
                addAnimation(Sections.ALL.Strobe(Color.Purple, 0.1))
            }.Else {
                If({ speakerShooterReady }) {
                    addAnimation(Sections.SPEAKER.Strobe(Color.Lime, 0.25))
                }.ElseIf({ ampShooterReady }) {
                    If({ alliance == Alliance.Red }) {
                        addAnimation(Sections.LEFT.Strobe(Color.Lime, 0.25))
                    }.Else {
                        addAnimation(Sections.RIGHT.Strobe(Color.Lime, 0.25))
                    }
                }
            }
        }
    }


    var alliance: Alliance? = null

    var noteInIntake = false
    var hasVisionTarget = false
    var limelightNotConnected = false
    var testEmergencySwitchActive = false

    var inSpeakerRange = false
    var speakerShooterReady = false
    var ampShooterReady = false

    var driverstationDisabled = false
    var driverstationAutonomous = false
    var driverstationTeleop = false

    override fun periodic() {
        noteInIntake = RobotState.notePosition.isIntake
        hasVisionTarget = Vision.hasVisionTarget()
        limelightNotConnected = !Vision.connected
        testEmergencySwitchActive = DSSwitches.isTestSwitchActive
        driverstationDisabled = DriverStation.isDisabled()
        driverstationAutonomous = DriverStation.isAutonomousEnabled()
        driverstationTeleop = DriverStation.isTeleopEnabled()
        alliance = State.alliance
        inSpeakerRange = (RobotPosition.distanceToSpeaker() < 3.0) && noteInIntake

        animationScope.update()
    }
}