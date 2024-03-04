package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.util.Color
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.subsystems.led.BaseLEDCommands.solid
import org.team9432.robot.subsystems.led.BaseLEDCommands.strobe

object LEDCommands {
    fun testMode() = SimpleCommand(
        requirements = LEDSubsystems.ALL,
        execute = {
            solid(Color.kRed, LEDs.Strip.SPEAKER_RIGHT_BOTTOM)
            solid(Color.kOrange, LEDs.Strip.SPEAKER_RIGHT_TOP)

            solid(Color.kBlue, LEDs.Strip.SPEAKER_LEFT_BOTTOM)
            solid(Color.kAqua, LEDs.Strip.SPEAKER_LEFT_TOP)

            solid(Color.kGreen, LEDs.Strip.AMP_LEFT_BOTTOM)
            solid(Color.kLime, LEDs.Strip.AMP_LEFT_TOP)

            solid(Color.kYellow, LEDs.Strip.AMP_RIGHT_BOTTOM)
            solid(Color.kLightYellow, LEDs.Strip.AMP_RIGHT_TOP)
        }
    )

    fun testBottom() = SimpleCommand(
        requirements = LEDSubsystems.BOTTOM,
        execute = {
            strobe(Color.kPurple, 0.2, LEDs.Strip.SPEAKER_RIGHT_BOTTOM)
            strobe(Color.kPurple, 0.2, LEDs.Strip.SPEAKER_LEFT_BOTTOM)
            strobe(Color.kPurple, 0.2, LEDs.Strip.AMP_LEFT_BOTTOM)
            strobe(Color.kPurple, 0.2, LEDs.Strip.AMP_RIGHT_BOTTOM)
        }
    )
}
