package org.team9432

import edu.wpi.first.hal.FRCNetComm
import edu.wpi.first.hal.HAL
import org.team9432.lib.logger.Logger
import org.team9432.robot.auto.AutoBuilder
import org.team9432.robot.auto.AutoChooser
import org.team9432.robot.commands.DefaultCommands
import org.team9432.robot.led.LEDs
import org.team9432.robot.oi.Controls
import org.team9432.robot.sensors.beambreaks.Beambreaks
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.sensors.vision.Vision
import org.team9432.robot.subsystems.amp.Amp
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.hood.Hood
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake
import org.team9432.robot.subsystems.shooter.Shooter

object Init {
    fun initRobot() {
        LEDs.startLoadingThread()
        Logger.initAdvantagekit("2024 - Sonic")

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
        Hopper
        Intake
        Shooter

        AutoBuilder.initChoosers()
        AutoChooser.initChooser()

        LEDs.stopLoadingThread()
    }
}