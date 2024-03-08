package org.team9432.robot.subsystems.led.animations

import edu.wpi.first.wpilibj.util.Color
import org.team9432.robot.subsystems.led.LEDModes
import org.team9432.robot.subsystems.led.LEDs

object Chase: LEDAnimation {
    private var chasePosition = 0
    private var chaseLoopCount = 0

    override fun updateBuffer(): Boolean {
        if (chasePosition > chaseOrder.size) chasePosition = 0 // Reset at the end

        // Set everything to off, then make one white
        LEDModes.solid(Color.kBlack, LEDs.Strip.ALL)
        LEDs.buffer.setLED(chaseOrder[chasePosition], Color.kWhite)

        // every 10 loops (~0.2 seconds) move the light
        if (chaseLoopCount % 10 == 0) chasePosition++

        chaseLoopCount++

        return false
    }

    override fun reset() {
        chasePosition = 0
        chaseLoopCount = 0
    }

    private val chaseOrder =
        LEDs.Strip.SPEAKER_LEFT.indices.reversed() +
                LEDs.Strip.AMP_RIGHT.indices.reversed() +
                LEDs.Strip.AMP_RIGHT.indices +
                LEDs.Strip.TOP_BAR.indices +
                LEDs.Strip.SPEAKER_RIGHT.indices.reversed() +
                LEDs.Strip.SPEAKER_RIGHT.indices +
                LEDs.Strip.SPEAKER_LEFT.indices +
                LEDs.Strip.SPEAKER_LEFT.indices.reversed() +
                LEDs.Strip.TOP_BAR.indices.reversed() +
                LEDs.Strip.SPEAKER_LEFT.indices
}