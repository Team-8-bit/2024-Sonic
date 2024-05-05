package org.team9432.lib.led.strip

import org.team9432.lib.led.color.Color

/** An interface that defines interactions from the main code to device-specific led strips. */
interface NativeLedStrip {
    /** The number of leds in this strip. */
    val ledCount: Int

    /** Sets the led at the specified index to the specified color. */
    fun setLed(index: Int, color: Color)

    /** Displays the currently set colors on the strip. */
    fun render()
}