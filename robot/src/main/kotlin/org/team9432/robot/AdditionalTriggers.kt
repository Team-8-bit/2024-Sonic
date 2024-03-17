package org.team9432.robot

import edu.wpi.first.wpilibj.DriverStation
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.input.KTrigger
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.Rocket

object AdditionalTriggers {
    private val preloadAnimation = KTrigger { DriverStation.isDisabled() && RobotState.noteInSpeakerSideHopperBeambreak() }

    init {
        preloadAnimation.onTrue(InstantCommand { LEDState.animation = Rocket(0.5, color = Rocket.HSVColor(42, 255, 255)) })
    }
}
