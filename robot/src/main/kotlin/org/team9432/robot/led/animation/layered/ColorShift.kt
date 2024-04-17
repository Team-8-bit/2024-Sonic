package org.team9432.robot.led.animation.layered

import org.team9432.lib.unit.Time
import org.team9432.lib.unit.seconds
import org.team9432.robot.led.animation.RunningAnimation
import org.team9432.robot.led.animation.groups.SequentialAnimation
import org.team9432.robot.led.animation.groups.repeat
import org.team9432.robot.led.animation.simple.FadeToColor
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.strip.Section

fun ColorShift(section: Section, colors: List<Color>, colorDuration: Time = 3.seconds, speed: Int = 5, priority: Int = 99) =
    SequentialAnimation(
        *colors.map { RunningAnimation(FadeToColor(it, colorDuration, speed, section), priority) }.toTypedArray()
    ).repeat()
