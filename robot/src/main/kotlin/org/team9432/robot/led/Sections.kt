package org.team9432.robot.led

import org.team9432.lib.led.strip.Section

object Sections {
    val SPEAKER_LEFT_TOP = Section((0..11).toSet())
    val SPEAKER_LEFT_BOTTOM = Section((12..21).toSet())

    val SPEAKER_RIGHT_BOTTOM = Section((22..33).toList().reversed().toSet())
    val SPEAKER_RIGHT_TOP = Section((34..43).toList().reversed().toSet())

    val AMP_LEFT_TOP = Section((44..55).toSet())
    val AMP_LEFT_BOTTOM = Section((56..65).toSet())

    val AMP_RIGHT_BOTTOM = Section((66..77).toList().reversed().toSet())
    val AMP_RIGHT_TOP = Section((78..87).toList().reversed().toSet())

    val TOP_LEFT = Section((88..102).toSet())
    val TOP_RIGHT = Section((103..117).toSet())

    val TOP_BAR = TOP_LEFT + TOP_RIGHT

    val SPEAKER_LEFT = SPEAKER_LEFT_TOP + SPEAKER_LEFT_BOTTOM
    val SPEAKER_RIGHT = SPEAKER_RIGHT_TOP + SPEAKER_RIGHT_BOTTOM
    val AMP_LEFT = AMP_LEFT_TOP + AMP_LEFT_BOTTOM
    val AMP_RIGHT = AMP_RIGHT_TOP + AMP_RIGHT_BOTTOM

    val LEFT = SPEAKER_LEFT + AMP_RIGHT
    val RIGHT = SPEAKER_RIGHT + AMP_LEFT

    val SPEAKER = SPEAKER_LEFT + SPEAKER_RIGHT
    val AMP = AMP_LEFT + AMP_RIGHT

    val ALL_BUT_TOP = SPEAKER + AMP
    val ALL = ALL_BUT_TOP + TOP_BAR
}