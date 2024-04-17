package org.team9432.robot.led.animation.groups

import org.team9432.robot.led.animation.AnimationJob

fun AnimationJob.repeat() = RepeatAnimation(this)