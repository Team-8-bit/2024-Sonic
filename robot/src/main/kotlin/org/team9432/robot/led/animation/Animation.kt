package org.team9432.robot.led.animation

interface Animation {
    /** Update the animation, returns true when finished **/
    fun update(): Boolean

    fun start() {}
    fun end() {}
}