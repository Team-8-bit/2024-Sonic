package org.team9432.lib.led.animation.layered

import org.team9432.lib.led.animation.groups.SequentialAnimation
import org.team9432.lib.led.animation.groups.repeat
import org.team9432.lib.led.animation.simple.FadeToColor
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.seconds

fun ColorShift(section: Section, colors: List<Color>, colorDuration: Time = 3.seconds, speed: Int = 5, priority: Int? = null, name: String = "") = SequentialAnimation(
    *colors.map { FadeToColor(it, colorDuration, speed, section).withPriority(priority).withName(name) }.toTypedArray()
).repeat()
