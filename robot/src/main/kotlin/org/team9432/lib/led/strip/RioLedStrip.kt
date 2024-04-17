package org.team9432.lib.led.strip

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import org.team9432.lib.led.color.Color

class RioLedStrip(override val ledCount: Int, pwmPort: Int): NativeLedStrip {
    private val controller = AddressableLED(pwmPort)
    private val buffer = AddressableLEDBuffer(ledCount)

    init {
        controller.setLength(ledCount)
        controller.start()
    }

    override fun setLed(index: Int, color: Color) = buffer.setLED(index, color.toWPILibColor())
    override fun render() = controller.setData(buffer)
}