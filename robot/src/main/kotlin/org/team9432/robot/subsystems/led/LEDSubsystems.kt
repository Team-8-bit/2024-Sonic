package org.team9432.robot.subsystems.led

import org.team9432.lib.commandbased.KSubsystem

object LEDSubsystems {
    val SPEAKER_LEFT_BOTTOM = object: LEDSubsystem(LEDs.Strip.SPEAKER_LEFT_BOTTOM) {}
    val SPEAKER_LEFT_TOP = object: LEDSubsystem(LEDs.Strip.SPEAKER_LEFT_TOP) {}
    val SPEAKER_RIGHT_TOP = object: LEDSubsystem(LEDs.Strip.SPEAKER_RIGHT_TOP) {}
    val SPEAKER_RIGHT_BOTTOM = object: LEDSubsystem(LEDs.Strip.SPEAKER_RIGHT_BOTTOM) {}
    val AMP_LEFT_BOTTOM = object: LEDSubsystem(LEDs.Strip.AMP_LEFT_BOTTOM) {}
    val AMP_LEFT_TOP = object: LEDSubsystem(LEDs.Strip.AMP_LEFT_TOP) {}
    val AMP_RIGHT_TOP = object: LEDSubsystem(LEDs.Strip.AMP_RIGHT_TOP) {}
    val AMP_RIGHT_BOTTOM = object: LEDSubsystem(LEDs.Strip.AMP_RIGHT_BOTTOM) {}

    val SPEAKER_LEFT = setOf(SPEAKER_LEFT_TOP, SPEAKER_LEFT_BOTTOM)
    val SPEAKER_RIGHT = setOf(SPEAKER_RIGHT_TOP, SPEAKER_RIGHT_BOTTOM)
    val AMP_LEFT = setOf(AMP_LEFT_TOP, AMP_LEFT_BOTTOM)
    val AMP_RIGHT = setOf(AMP_RIGHT_TOP, AMP_RIGHT_BOTTOM)

    val SPEAKER = SPEAKER_LEFT + SPEAKER_RIGHT
    val AMP = AMP_LEFT + AMP_RIGHT

    val ALL = SPEAKER + AMP
}

abstract class LEDSubsystem(val strip: LEDs.Strip): KSubsystem()
