package org.team9432

import edu.wpi.first.hal.FRCNetComm
import edu.wpi.first.hal.HAL
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.team9432.lib.advantagekit.Logger
import org.team9432.lib.coroutines.RIODispatcher
import org.team9432.lib.coroutines.delay
import org.team9432.lib.coroutines.rioLaunch
import org.team9432.lib.dashboard.Dashboard
import org.team9432.lib.dashboard.doubleDashboardModule
import org.team9432.lib.led.management.AnimationManager
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.RioLedStrip
import org.team9432.lib.robot.RobotBase
import org.team9432.lib.unit.seconds
import org.team9432.robot.Devices
import org.team9432.robot.LEDState
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoChooser
import org.team9432.robot.auto.builder.AutoBuilder
import org.team9432.robot.commands.DefaultCommands
import org.team9432.robot.oi.Controls
import org.team9432.robot.sensors.beambreaks.Beambreaks
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.sensors.vision.Vision
import org.team9432.robot.subsystems.Amp
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure
import org.team9432.robot.subsystems.drivetrain.Drivetrain

object Init {
    /** Preforms all initialization needed. */
    fun initRobot() {
        LEDStrip.create(RioLedStrip(118, Devices.LED_PORT))

        RobotBase.coroutineScope.rioLaunch {
            Dashboard.run(RIODispatcher)
        }

        RobotBase.coroutineScope.launch {
            var count by doubleDashboardModule("count", 0.0)

            while (isActive) {
                delay(1.seconds)
                count++
            }
        }

        AnimationManager
        LEDState

        Logger.initAdvantagekit("2024 - Sonic")

        HAL.report(FRCNetComm.tResourceType.kResourceType_Framework, FRCNetComm.tInstances.kFramework_AdvantageKit)
        HAL.report(FRCNetComm.tResourceType.kResourceType_Language, FRCNetComm.tInstances.kLanguage_Kotlin)

        DefaultCommands.setDefaultCommands()
        Controls.setButtons()
        Vision.forwardPorts()

        Vision
        Gyro
        Beambreaks

        Amp
        Drivetrain
        Hood
        Superstructure
        Shooter

        AutoBuilder.initChoosers()
        AutoChooser.initChooser()

        RobotState.findNote()?.let { RobotState.notePosition = it }
    }
}