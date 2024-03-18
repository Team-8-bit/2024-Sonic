package org.team9432.robot.led.animations

interface LEDAnimation {
    /** Update the animation, returns true when finished **/
    fun updateBuffer(): Boolean

    fun reset() {}
}