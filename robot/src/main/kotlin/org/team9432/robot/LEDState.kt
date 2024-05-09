package org.team9432.robot

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.DriverStation.Alliance
import org.team9432.Robot
import org.team9432.dashboard.lib.widgets.delegates.readableDashboardBoolean
import org.team9432.dashboard.lib.widgets.delegates.readableDashboardString
import org.team9432.dashboard.lib.widgets.delegates.writableDashboardBoolean
import org.team9432.lib.State
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.coroutines.repeat
import org.team9432.lib.coroutines.runParallel
import org.team9432.lib.coroutines.runSequential
import org.team9432.lib.coroutines.wait
import org.team9432.lib.led.animations.*
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.predefined.*
import org.team9432.lib.led.management.AnimationBindScope
import org.team9432.lib.led.management.Section
import org.team9432.lib.unit.meters
import org.team9432.lib.unit.seconds
import org.team9432.robot.oi.switches.DSSwitches
import org.team9432.robot.sensors.vision.Vision

object LEDState: KPeriodic() {

    /* -------- LED Sections -------- */

    private val speakerLeftTop = Section((0..11).toSet())
    private val speakerLeftBottom = Section((12..21).toSet())

    private val speakerRightBottom = Section((22..33).toList().reversed().toSet())
    private val speakerRightTop = Section((34..43).toList().reversed().toSet())

    private val ampLeftTop = Section((44..55).toSet())
    private val ampLeftBottom = Section((56..65).toSet())

    private val ampRightBottom = Section((66..77).toList().reversed().toSet())
    private val ampRightTop = Section((78..87).toList().reversed().toSet())

    private val topLeft = Section((88..102).toSet())
    private val topRight = Section((103..117).toSet())

    private val topBar = topLeft + topRight

    private val speakerLeft = speakerLeftTop + speakerLeftBottom
    private val speakerRight = speakerRightTop + speakerRightBottom
    private val ampLeft = ampLeftTop + ampLeftBottom
    private val ampRight = ampRightTop + ampRightBottom

    private val left = speakerLeft + ampRight
    private val right = speakerRight + ampLeft

    private val speaker = speakerLeft + speakerRight
    private val amp = ampLeft + ampRight

    private val all = speaker + amp + topBar


    /* -------- Animation States -------- */

    private val animationScope = AnimationBindScope.build {
        If({ ledTest }) {
            setAnimation(
                runParallel(
                    speakerLeft.solid(Color.Green),
                    speakerRight.solid(Color.Red),
                    ampLeft.solid(Color.Blue),
                    ampRight.solid(Color.Yellow),
                    topBar.solid(Color.White),
                )
            )
        }.ElseIf({ driverstationDisabled && !Robot.hasBeenEnabled }) {
            setAnimation(
                runSequential(
                    runParallel(
                        speakerLeft.solid(Color.White, 0.5.seconds),
                        speakerRight.solid(Color.White, 0.5.seconds),
                        ampLeft.solid(Color.White, 0.5.seconds),
                        ampRight.solid(Color.White, 0.5.seconds),
                    ),
                    runParallel(
                        speakerLeft.fadeToColor(Color.Black, 2.seconds, 5),
                        speakerRight.fadeToColor(Color.Black, 2.seconds, 5),
                        ampLeft.fadeToColor(Color.Black, 2.seconds, 5),
                        ampRight.fadeToColor(Color.Black, 2.seconds, 5),
                    ),
                    wait(0.5.seconds),
                    runParallel(
                        speakerLeft.pulse(Color.White, 2.seconds),
                        speakerRight.pulse(Color.White, 2.seconds),
                        ampLeft.pulse(Color.White, 2.seconds),
                        ampRight.pulse(Color.White, 2.seconds),
                    )
                )
            )

            If({ hasVisionTarget }) {
                If({ alliance == null }) {
                    setAnimation(topBar.breath(listOf(Color.Black, Color.White)))
                }.ElseIf({ alliance == Alliance.Red }) {
                    setAnimation(topBar.solid(Color.Red))
                }.ElseIf({ alliance == Alliance.Blue }) {
                    setAnimation(topBar.solid(Color.Blue))
                }
            }.Else {
                If({ limelightNotConnected }) {
                    setAnimation(
                        runSequential(
                            topBar.bounceToColor(Color.Red),
                            topBar.bounceToColor(Color.Black)
                        ).repeat()
                    )
                }.Else {
                    setAnimation(
                        runSequential(
                            topBar.solid(Color.Black),
                            topBar.slideToColor(Color.Green)
                        )
                    )
                }
            }
        }.ElseIf({ driverstationDisabled && Robot.hasBeenEnabled }) {
            setAnimation(
                runParallel(
                    speakerLeft.pulse(Color.White, 2.seconds),
                    speakerRight.pulse(Color.White, 2.seconds),
                    ampLeft.pulse(Color.White, 2.seconds),
                    ampRight.pulse(Color.White, 2.seconds),
                    topBar.solid(Color.Black)
                )
            )
        }.ElseIf({ driverstationAutonomous }) {
            setAnimation(all.strobe(Color.Red, 0.25.seconds))
        }.ElseIf({ driverstationTeleop }) {
            setAnimation(all.breath(Color.RainbowColors, 0.20.seconds, 5).withPriority(-100)) // Priority is low so this is covered by other animations

            If({ inSpeakerRange }) {
                setAnimation(speaker.strobe(Color.White, 0.5.seconds))
            }.ElseIf({ noteIndicatorLights }) {
                setAnimation(all.strobe(Color.Purple, 0.25.seconds))
            }.Else {
                If({ speakerShooterReady }) {
                    setAnimation(speaker.strobe(Color.Lime, 0.25.seconds))
                }.ElseIf({ ampShooterReady }) {
                    If({ alliance == Alliance.Red }) {
                        setAnimation(left.strobe(Color.Lime, 0.25.seconds))
                    }.Else {
                        setAnimation(right.strobe(Color.Lime, 0.25.seconds))
                    }
                }
            }
        }
    }


    /* -------- States -------- */

    private var allianceDashboard by readableDashboardString("Alliance", "Unknown", row = 1, col = 1, tab = "Testing")
    private var alliance: Alliance? = null

    private var noteInIntake = false
    private var hasVisionTarget = false
    private var limelightNotConnected = false
    private var testEmergencySwitchActive = false

    private var inSpeakerRange = false
    var speakerShooterReady = false
    var ampShooterReady = false

    private var driverstationDisabled by readableDashboardBoolean("Disabled", false, row = 1, col = 0, tab = "Testing")
    private var driverstationAutonomous by readableDashboardBoolean("Autonomous", false, row = 2, col = 0, tab = "Testing")
    private var driverstationTeleop by readableDashboardBoolean("Teleop", false, row = 0, col = 0, tab = "Testing")

    private var ledTest by writableDashboardBoolean("LEDTest", false, row = 0, col = 1, tab = "Testing")

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
        allianceDashboard = alliance.toString()

        inSpeakerRange = (RobotPosition.distanceToSpeaker() < 3.0.meters) && noteInIntake

        animationScope.update()
    }
}