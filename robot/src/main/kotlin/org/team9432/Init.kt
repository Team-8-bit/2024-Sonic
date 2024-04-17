package org.team9432

import edu.wpi.first.hal.FRCNetComm
import edu.wpi.first.hal.HAL
import org.team9432.lib.advantagekit.Logger
import org.team9432.lib.led.animation.AnimationManager
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.RioLedStrip
import org.team9432.robot.Devices
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoBuilder
import org.team9432.robot.auto.AutoChooser
import org.team9432.robot.commands.DefaultCommands
import org.team9432.robot.led.LEDState
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
    fun initRobot() {
        LEDStrip.create(RioLedStrip(118, Devices.LED_PORT))

        AnimationManager.startAsync()

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