package org.team9432.lib.led.animation.layered

import org.team9432.lib.led.animation.groups.repeat
import org.team9432.lib.led.animation.groups.sequentialAnimation
import org.team9432.lib.led.animation.simple.fadeToColor
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.strip.Section
import org.team9432.lib.unit.Time
import org.team9432.lib.unit.seconds

fun Section.colorShift(colors: List<Color>, colorDuration: Time = 3.seconds, speed: Int = 5, priority: Int? = null, name: String = "") = sequentialAnimation(
    *colors.map {
        fadeToColor(it, colorDuration, speed).withPriority(priority).withName(name)
    }.toTypedArray()
).repeat()
