package org.team9432.robot.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.DriverStation.Alliance
import org.team9432.Robot
import org.team9432.lib.State
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.led.animation.AnimationBindScope
import org.team9432.lib.led.animation.RunningAnimation
import org.team9432.lib.led.animation.groups.ParallelAnimation
import org.team9432.lib.led.animation.groups.SequentialAnimation
import org.team9432.lib.led.animation.groups.WaitAnimation
import org.team9432.lib.led.animation.groups.repeat
import org.team9432.lib.led.animation.layered.ColorShift
import org.team9432.lib.led.animation.simple.*
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.predefined.*
import org.team9432.lib.unit.seconds
import org.team9432.robot.RobotPosition
import org.team9432.robot.RobotState
import org.team9432.robot.oi.switches.DSSwitches
import org.team9432.robot.sensors.vision.Vision

object LEDState: KPeriodic() {
    private val animationScope = AnimationBindScope.build {
        If({ testEmergencySwitchActive }) {
            addAnimation(
                ParallelAnimation(
                    RunningAnimation(Solid(Color.Green, Sections.SPEAKER_LEFT)),
                    RunningAnimation(Solid(Color.Red, Sections.SPEAKER_RIGHT)),
                    RunningAnimation(Solid(Color.Blue, Sections.AMP_LEFT)),
                    RunningAnimation(Solid(Color.Yellow, Sections.AMP_RIGHT)),
                    RunningAnimation(Solid(Color.White, Sections.TOP_BAR)),
                )
            )
        }.ElseIf({ driverstationDisabled && !Robot.hasBeenEnabled }) {
            addAnimation(
                SequentialAnimation(
                    ParallelAnimation(
                        RunningAnimation(Solid(Color.White, Sections.SPEAKER_LEFT)),
                        RunningAnimation(Solid(Color.White, Sections.SPEAKER_RIGHT)),
                        RunningAnimation(Solid(Color.White, Sections.AMP_LEFT)),
                        RunningAnimation(Solid(Color.White, Sections.AMP_RIGHT))
                    ),
                    ParallelAnimation(
                        RunningAnimation(FadeToColor(Color.Black, 3.seconds, 5, Sections.SPEAKER_LEFT)),
                        RunningAnimation(FadeToColor(Color.Black, 3.seconds, 5, Sections.SPEAKER_RIGHT)),
                        RunningAnimation(FadeToColor(Color.Black, 3.seconds, 5, Sections.AMP_LEFT)),
                        RunningAnimation(FadeToColor(Color.Black, 3.seconds, 5, Sections.AMP_RIGHT)),
                    ),
                    WaitAnimation(1.seconds),
                    ParallelAnimation(
                        RunningAnimation(Pulse(Color.White, 2.seconds, 1.seconds, Sections.SPEAKER_LEFT)),
                        RunningAnimation(Pulse(Color.White, 2.seconds, 1.seconds, Sections.SPEAKER_RIGHT)),
                        RunningAnimation(Pulse(Color.White, 2.seconds, 1.seconds, Sections.AMP_LEFT)),
                        RunningAnimation(Pulse(Color.White, 2.seconds, 1.seconds, Sections.AMP_RIGHT)),
                    )
                )
            )

            If({ hasVisionTarget }) {
                If({ alliance == null }) {
                    addAnimation(ColorShift(Sections.TOP_BAR, listOf(Color.Black, Color.White)))
                }.ElseIf({ alliance == Alliance.Red }) {
                    addAnimation(RunningAnimation(Solid(Color.Red, Sections.TOP_BAR)))
                }.ElseIf({ alliance == Alliance.Blue }) {
                    addAnimation(RunningAnimation(Solid(Color.Blue, Sections.TOP_BAR)))
                }
            }.Else {
                If({ limelightNotConnected }) {
                    addAnimation(
                        SequentialAnimation(
                            RunningAnimation(Solid(Color.Black, Sections.TOP_BAR)),
                            SequentialAnimation(
                                RunningAnimation(BounceToColor(Color.Red, Sections.TOP_BAR)),
                                RunningAnimation(BounceToColor(Color.Black, Sections.TOP_BAR))
                            ).repeat()
                        )
                    )
                }.Else {
                    addAnimation(
                        SequentialAnimation(
                            RunningAnimation(Solid(Color.Black, Sections.TOP_BAR)),
                            RunningAnimation(SlideToColor(Color.Green, Sections.TOP_BAR))
                        )
                    )
                }
            }
        }.ElseIf({ driverstationDisabled && Robot.hasBeenEnabled }) {
            addAnimation(
                ParallelAnimation(
                    RunningAnimation(Pulse(Color.White, 2.seconds, 1.seconds, Sections.SPEAKER_LEFT)),
                    RunningAnimation(Pulse(Color.White, 2.seconds, 1.seconds, Sections.SPEAKER_RIGHT)),
                    RunningAnimation(Pulse(Color.White, 2.seconds, 1.seconds, Sections.AMP_LEFT)),
                    RunningAnimation(Pulse(Color.White, 2.seconds, 1.seconds, Sections.AMP_RIGHT)),
                )
            )
        }.ElseIf({ driverstationAutonomous }) {
            addAnimation(RunningAnimation(Strobe(Color.Red, 0.25.seconds, Sections.ALL)))
        }.ElseIf({ driverstationTeleop }) {
            addAnimation(ColorShift(Sections.ALL, Color.RainbowColors, 0.20.seconds, 5, priority = 100))

            // I think this will keep rainbowing even if there are other animations running
            If({ inSpeakerRange }) {
                addAnimation(RunningAnimation(Strobe(Color.White, 0.5.seconds, Sections.SPEAKER)))
            }.ElseIf({ noteIndicatorLights }) {
                addAnimation(RunningAnimation(Strobe(Color.Purple, 0.25.seconds, Sections.ALL)))
            }.Else {
                If({ speakerShooterReady }) {
                    addAnimation(RunningAnimation(Strobe(Color.Lime, 0.25.seconds, Sections.SPEAKER)))
                }.ElseIf({ ampShooterReady }) {
                    If({ alliance == Alliance.Red }) {
                        addAnimation(RunningAnimation(Strobe(Color.Lime, 0.25.seconds, Sections.LEFT)))
                    }.Else {
                        addAnimation(RunningAnimation(Strobe(Color.Lime, 0.25.seconds, Sections.RIGHT)))
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

    var noteIndicatorLights = false

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