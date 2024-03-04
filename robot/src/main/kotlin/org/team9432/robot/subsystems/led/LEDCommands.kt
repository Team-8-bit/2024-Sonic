package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.util.Color
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.subsystems.led.BaseLEDCommands.solid

object LEDCommands {
    fun testMode() = SimpleCommand(
        requirements = LEDSubsystems.ALL,
        execute = {
            solid(Color.kRed, LEDs.Strip.SPEAKER_RIGHT_TOP)
            solid(Color.kOrange, LEDs.Strip.SPEAKER_RIGHT_BOTTOM)

            solid(Color.kBlue, LEDs.Strip.SPEAKER_LEFT_TOP)
            solid(Color.kAqua, LEDs.Strip.SPEAKER_LEFT_BOTTOM)

            solid(Color.kGreen, LEDs.Strip.AMP_LEFT_TOP)
            solid(Color.kLime, LEDs.Strip.AMP_LEFT_BOTTOM)

            solid(Color.kYellow, LEDs.Strip.AMP_RIGHT_TOP)
            solid(Color.kLightYellow, LEDs.Strip.AMP_RIGHT_BOTTOM)
        }
    )
}
