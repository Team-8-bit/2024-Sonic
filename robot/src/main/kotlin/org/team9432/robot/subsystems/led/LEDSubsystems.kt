package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.util.Color
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.commandbased.commands.runsWhenDisabled

object LEDSubsystems {
    val SPEAKER_LEFT_TOP = object: LEDSubsystem(LEDs.Strip.SPEAKER_LEFT_TOP) {}
    val SPEAKER_LEFT_BOTTOM = object: LEDSubsystem(LEDs.Strip.SPEAKER_LEFT_BOTTOM) {}
    val SPEAKER_RIGHT_BOTTOM = object: LEDSubsystem(LEDs.Strip.SPEAKER_RIGHT_BOTTOM) {}
    val SPEAKER_RIGHT_TOP = object: LEDSubsystem(LEDs.Strip.SPEAKER_RIGHT_TOP) {}
    val AMP_LEFT_TOP = object: LEDSubsystem(LEDs.Strip.AMP_LEFT_TOP) {}
    val AMP_LEFT_BOTTOM = object: LEDSubsystem(LEDs.Strip.AMP_LEFT_BOTTOM) {}
    val AMP_RIGHT_BOTTOM = object: LEDSubsystem(LEDs.Strip.AMP_RIGHT_BOTTOM) {}
    val AMP_RIGHT_TOP = object: LEDSubsystem(LEDs.Strip.AMP_RIGHT_TOP) {}

    val SPEAKER_LEFT = setOf(SPEAKER_LEFT_BOTTOM, SPEAKER_LEFT_TOP)
    val SPEAKER_RIGHT = setOf(SPEAKER_RIGHT_BOTTOM, SPEAKER_RIGHT_TOP)
    val AMP_LEFT = setOf(AMP_LEFT_BOTTOM, AMP_LEFT_TOP)
    val AMP_RIGHT = setOf(AMP_RIGHT_BOTTOM, AMP_RIGHT_TOP)

    val TOP = setOf(SPEAKER_LEFT_TOP, SPEAKER_RIGHT_TOP, AMP_LEFT_TOP, AMP_RIGHT_TOP)
    val BOTTOM = setOf(SPEAKER_LEFT_BOTTOM, SPEAKER_RIGHT_BOTTOM, AMP_LEFT_BOTTOM, AMP_RIGHT_BOTTOM)

    val SPEAKER = SPEAKER_LEFT + SPEAKER_RIGHT
    val AMP = AMP_LEFT + AMP_RIGHT

    val ALL = SPEAKER + AMP

    init {
        ALL.forEach { subsystem ->
            subsystem.defaultCommand = SimpleCommand(
                requirements = setOf(subsystem),
                execute = {
                    if (DriverStation.isDisabled()) {
                        BaseLEDCommands.breath(LEDColors.MAIN_GREEN, Color.kBlack, LEDs.Strip.ALL, 3.0)
                    } else if (DriverStation.isAutonomous()) {
                        BaseLEDCommands.strobe(Color.kRed, 0.25, LEDs.Strip.ALL)
                    } else { // Teleop
                        BaseLEDCommands.rainbow(30.0, 0.5, LEDs.Strip.ALL) // This will be the default unless overwritten later
                    }
                }
            ).runsWhenDisabled(true)
        }
    }
}

abstract class LEDSubsystem(val strip: LEDs.Strip): KSubsystem()
