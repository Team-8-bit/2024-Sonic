package org.team9432.robot.led.animations.predefined.layered

import org.team9432.robot.led.LEDSection
import org.team9432.robot.led.animations.RepeatAnimationGroup
import org.team9432.robot.led.animations.SequentialAnimationGroup
import org.team9432.robot.led.animations.predefined.simple.FadeToColor
import org.team9432.robot.led.color.Color

fun ColorShift(section: LEDSection, colors: List<Color>, colorDuration: Double = 3.0, speed: Int = 5) =
    RepeatAnimationGroup(
        SequentialAnimationGroup(
            *colors.map { FadeToColor(section, it, colorDuration, speed) }.toTypedArray()
        )
    )
