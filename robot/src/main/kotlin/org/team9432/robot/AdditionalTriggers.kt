package org.team9432.robot

import edu.wpi.first.wpilibj.DriverStation
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.PrintCommand
import org.team9432.lib.commandbased.input.KTrigger
import org.team9432.robot.commands.hood.HoodAimAtSpeaker
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.Rocket

object AdditionalTriggers {
    private val preloadAnimation = KTrigger { DriverStation.isDisabled() && RobotState.noteInSpeakerSideHopperBeambreak() }

    private val hoodTrigger = KTrigger { DriverStation.isAutonomous() }

    init {
        hoodTrigger.whileTrue(HoodAimAtSpeaker())
        preloadAnimation.onTrue(InstantCommand { LEDState.animation = Rocket(0.5, color = Rocket.HSVColor(42, 255, 255)) })
    }
}
