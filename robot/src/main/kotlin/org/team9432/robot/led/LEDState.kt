package org.team9432.robot.led

import edu.wpi.first.wpilibj.DriverStation
import org.team9432.lib.State
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.robot.RobotState
import org.team9432.robot.led.animations.Animation
import org.team9432.robot.led.animations.ParallelAnimationGroup
import org.team9432.robot.led.animations.predefined.simple.Pulse
import org.team9432.robot.led.animations.predefined.simple.Solid
import org.team9432.robot.led.animations.predefined.simple.Strobe
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.color.presets.*
import org.team9432.robot.oi.EmergencySwitches
import org.team9432.robot.sensors.vision.Vision

object LEDState: KPeriodic() {
    var allianceColor = Color.White

    var noteInIntake = false
    var hasVisionTarget = false
    var limelightConnected = false
    var testEmergencySwitchActive = false

    var speakerShooterReady = false
    var ampShooterReady = false

    var driverstationDisabled = false
    var driverstationAutonomous = false
    var driverstationTeleop = false

    private val noteInIntakeSupplier = { noteInIntake }
    private val hasVisionTargetSupplier = { hasVisionTarget }
    private val limelightConnectedSupplier = { limelightConnected }
    private val testEmergencySwitchActiveSupplier = { testEmergencySwitchActive }

    private val speakerShooterReadySupplier = { speakerShooterReady }
    private val ampShooterReadySupplier = { ampShooterReady }
    private val driverstationDisabledSupplier = { driverstationDisabled }
    private val driverstationAutonomousSupplier = { driverstationAutonomous }
    private val driverstationTeleopSupplier = { driverstationTeleop }

    var animation: Animation? = null
        set(value) {
            value?.start()
            field = value
        }

    private val testAnimation = ParallelAnimationGroup(
        Solid(LEDs.Section.SPEAKER_LEFT, Color.Green),
        Solid(LEDs.Section.SPEAKER_RIGHT, Color.Red),
        Solid(LEDs.Section.AMP_LEFT, Color.Blue),
        Solid(LEDs.Section.AMP_RIGHT, Color.Yellow),
        Solid(LEDs.Section.TOP_BAR, Color.White)
    )

    private val pulseAnimation = ParallelAnimationGroup(
        Pulse(LEDs.Section.SPEAKER_LEFT, Color.White, 2.0, 1.0),
        Pulse(LEDs.Section.SPEAKER_RIGHT, Color.White, 2.0, 1.0),
        Pulse(LEDs.Section.AMP_LEFT, Color.White, 2.0, 1.0),
        Pulse(LEDs.Section.AMP_RIGHT, Color.White, 2.0, 1.0),
    )

    class AnimationBindScope(val enabled: () -> Boolean) {
    }

    fun bindAnimations(bind: AnimationBindScope.() -> Unit) {
        AnimationBindScope { true }.bind()
    }

    fun AnimationBindScope.kIf(enabled: () -> Boolean, bind: AnimationBindScope.() -> Unit): AnimationBindScope {
        val scope = AnimationBindScope { this.enabled.invoke() && enabled.invoke() }
        scope.bind()
        return scope
    }

    fun AnimationBindScope.kElseIf(enabled: () -> Boolean, bind: AnimationBindScope.() -> Unit): AnimationBindScope {
        val scope = AnimationBindScope { !this.enabled.invoke() && enabled.invoke() }
        scope.bind()
        return scope
    }

    fun AnimationBindScope.kElse(bind: AnimationBindScope.() -> Unit): AnimationBindScope {
        val scope = AnimationBindScope { !this.enabled.invoke() }
        scope.bind()
        return scope
    }

    val periodicChecks = mutableListOf<() -> Unit>()
    val booleanTrackingMap = mutableMapOf<Int, Boolean>()

    fun AnimationBindScope.bindAnimation(animation: Animation) {
        val booleanSupplier = this.enabled
        periodicChecks.add {
            val isActive = booleanSupplier.invoke()
            val wasActiveLast = booleanTrackingMap.getOrPut(booleanSupplier.hashCode()) { !isActive }
            if (isActive && !wasActiveLast) AnimationManager.addAnimation(animation)
            else if (wasActiveLast && !isActive) AnimationManager.stopAnimation(animation)

            booleanTrackingMap[booleanSupplier.hashCode()] = isActive
        }
    }

    init {
        bindAnimations {
            kIf(testEmergencySwitchActiveSupplier) {
                bindAnimation(testAnimation)
            }.kElseIf(driverstationDisabledSupplier) {
                bindAnimation(pulseAnimation)
            }.kElseIf(driverstationAutonomousSupplier) {
                bindAnimation(Strobe(LEDs.Section.ALL, Color.Red, 0.25))
            }.kElseIf(driverstationTeleopSupplier) {
                bindAnimation(Solid(LEDs.Section.BOTTOM, Color.FloralWhite))
            }
        }

//        if (animation != null) {
//            animation?.let { animation ->
//                val isFinished = animation.update()
//                if (isFinished) LEDState.animation = null
//            }
//        } else if (testEmergencySwitchActive) {
//            testAnimation.update()
//        } else {
//            if (DriverStation.isDisabled()) {
//                AnimationManager.addAnimation(pulseAnimation)
//
//                // Set the top to the alliance color, this also shows when the fms is connected
//                if (State.alliance == null) {
//                    breath(Color.White, Color.Black, LEDs.Section.TOP_BAR, duration = 2.0)
//                } else {
//                    solid(allianceColor, LEDs.Section.TOP_BAR)
//                }
//
//                if (hasVisionTarget) { // Turn green when the robot can see an apriltag
//                    solid(LEDColors.MAIN_GREEN, LEDs.Section.TOP)
//                }
//
//                if (!limelightConnected) { // Lime and red when the limelight isn't connected
//                    breath(Color.Lime, Color.Red, LEDs.Section.BOTTOM, duration = 0.75)
//                }
//            } else if (DriverStation.isAutonomous()) {
//                strobe(Color.Red, 0.25, LEDs.Section.ALL)
//            } else { // Teleop
//                rainbow(30.0, 0.5, LEDs.Section.ALL) // This will be the default unless overwritten later
//
//                if (noteInIntake) { // Blink purple when there's a note in the intake
//                    strobe(Color.Purple, 0.1, LEDs.Section.ALL)
//                }
//
//                if (speakerShooterReady) {
//                    strobe(Color.Lime, 0.25, LEDs.Section.SPEAKER)
//                }
//                if (ampShooterReady) {
//                    if (State.alliance == DriverStation.Alliance.Red) {
//                        strobe(Color.Lime, 0.25, LEDs.Section.LEFT)
//                    } else {
//                        strobe(Color.Lime, 0.25, LEDs.Section.RIGHT)
//                    }
//                }
//            }
//        }
    }


    override fun periodic() {
        noteInIntake = RobotState.notePosition.isIntake
        hasVisionTarget = Vision.hasVisionTarget()
        limelightConnected = Vision.connected
        testEmergencySwitchActive = EmergencySwitches.testSwitchActive
        driverstationDisabled = DriverStation.isDisabled()
        driverstationAutonomous = DriverStation.isAutonomousEnabled()
        driverstationTeleop = DriverStation.isTeleopEnabled()

        allianceColor = when (State.alliance) {
            DriverStation.Alliance.Red -> Color.Red
            DriverStation.Alliance.Blue -> Color.Blue
            null -> Color.White
        }

        periodicChecks.forEach { it.invoke() }
    }
}