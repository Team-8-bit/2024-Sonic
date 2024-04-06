package org.team9432.robot.led.animation.layered

import org.team9432.robot.led.animation.groups.RepeatAnimationGroup
import org.team9432.robot.led.animation.groups.SequentialAnimationGroup
import org.team9432.robot.led.animation.simple.FadeToColor
import org.team9432.robot.led.color.Color
import org.team9432.robot.led.strip.Section

fun ColorShift(section: Section, colors: List<Color>, colorDuration: Double = 3.0, speed: Int = 5) =
    RepeatAnimationGroup(
        SequentialAnimationGroup(
            *colors.map { FadeToColor(section, it, colorDuration, speed) }.toTypedArray()
        )
    )
