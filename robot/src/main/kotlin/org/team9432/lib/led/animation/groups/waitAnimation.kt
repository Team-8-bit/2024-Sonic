package org.team9432.lib.led.animation.groups

import org.team9432.lib.delay
import org.team9432.lib.led.animation.AnimationJob
import org.team9432.lib.unit.Time

fun waitAnimation(time: Time) = AnimationJob { delay(time) }