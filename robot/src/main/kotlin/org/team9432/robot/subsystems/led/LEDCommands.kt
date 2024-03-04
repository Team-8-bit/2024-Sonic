package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.util.Color
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.subsystems.led.BaseLEDCommands.breath
import org.team9432.robot.subsystems.led.BaseLEDCommands.rainbow
import org.team9432.robot.subsystems.led.BaseLEDCommands.solid
import org.team9432.robot.subsystems.led.BaseLEDCommands.strobe

object LEDCommands {
    private val MAIN_GREEN = Color(0.05, 1.0, 0.1)

    init {
        LEDSubsystems.ALL.forEach { subsystem ->
            subsystem.defaultCommand = SimpleCommand(
                requirements = setOf(subsystem),
                execute = {
                    if (DriverStation.isDisabled()) {
                        breath(MAIN_GREEN, Color.kBlack, LEDs.Strip.ALL, 3.0)
                    } else if (DriverStation.isAutonomous()) {
                        strobe(Color.kRed, 0.25, LEDs.Strip.ALL)
                    } else { // Teleop
                        rainbow(30.0, 0.5, LEDs.Strip.ALL) // This will be the default unless overwritten later
                    }
                }
            )
        }
    }

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
