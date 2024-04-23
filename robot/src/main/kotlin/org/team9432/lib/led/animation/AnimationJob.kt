package org.team9432.lib.led.animation

fun interface AnimationJob {
    suspend fun run()
}