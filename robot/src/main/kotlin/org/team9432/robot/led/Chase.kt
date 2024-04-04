package org.team9432.robot.led

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.util.Color

object Chase {
//    private const val STEP_TIME = 0.05
//
//    fun updateBuffer(timestamp: Double = Timer.getFPGATimestamp()) {
//        val currentStep = ((timestamp % chaseCycleTime) / STEP_TIME).toInt()
//
//        // Set everything to off, then make one white
//        LEDModes.solid(Color.kBlack, LEDs.Section.ALL)
//        LEDs.buffer.setLED(chaseOrder[currentStep], Color.kWhite)
//    }
//
//    private val chaseOrder =
//        LEDs.Section.SPEAKER_LEFT.reversed() +
//                LEDs.Section.AMP_RIGHT.reversed() +
//                LEDs.Section.AMP_RIGHT +
//                LEDs.Section.TOP_BAR +
//                LEDs.Section.AMP_LEFT +
//                LEDs.Section.AMP_LEFT.reversed() +
//                LEDs.Section.SPEAKER_RIGHT.reversed() +
//                LEDs.Section.SPEAKER_RIGHT +
//                LEDs.Section.TOP_BAR.reversed() +
//                LEDs.Section.SPEAKER_LEFT
//
//    private val chaseLength = chaseOrder.size
//    private val chaseCycleTime = chaseLength * STEP_TIME
}