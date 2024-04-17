package org.team9432.lib.led.strip

import org.team9432.lib.led.color.Color

interface NativeLedStrip {
    val ledCount: Int
    fun setLed(index: Int, color: Color)
    fun render()
}