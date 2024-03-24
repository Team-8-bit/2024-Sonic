package org.team9432.robot.led

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj.Notifier
import org.team9432.LOOP_PERIOD_SECS
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.Devices


object LEDs: KSubsystem() {
    private const val LENGTH = 118

    private val ledController = AddressableLED(Devices.LED_PORT)
    val buffer = AddressableLEDBuffer(LENGTH)

    object Section {
        val SPEAKER_LEFT_TOP = (0..11).toList()
        val SPEAKER_LEFT_BOTTOM = (12..21).toList()

        val SPEAKER_RIGHT_BOTTOM = (22..33).toList()
        val SPEAKER_RIGHT_TOP = (34..43).toList()

        val AMP_LEFT_TOP = (44..55).toList()
        val AMP_LEFT_BOTTOM = (56..65).toList()

        val AMP_RIGHT_BOTTOM = (66..77).toList()
        val AMP_RIGHT_TOP = (78..87).toList()

        val TOP_BAR = (88..117).toList()

        val SPEAKER_LEFT = SPEAKER_LEFT_TOP + SPEAKER_LEFT_BOTTOM
        val SPEAKER_RIGHT = SPEAKER_RIGHT_BOTTOM + SPEAKER_RIGHT_TOP
        val AMP_LEFT = AMP_LEFT_TOP + AMP_LEFT_BOTTOM
        val AMP_RIGHT = AMP_RIGHT_BOTTOM + AMP_RIGHT_TOP

        val BOTTOM = SPEAKER_LEFT_BOTTOM + SPEAKER_RIGHT_BOTTOM + AMP_LEFT_BOTTOM + AMP_RIGHT_BOTTOM
        val TOP = SPEAKER_LEFT_TOP + SPEAKER_RIGHT_TOP + AMP_LEFT_TOP + AMP_RIGHT_TOP

        val LEFT = SPEAKER_LEFT + AMP_RIGHT
        val RIGHT = SPEAKER_RIGHT + AMP_LEFT

        val SPEAKER = SPEAKER_LEFT + SPEAKER_RIGHT
        val AMP = AMP_LEFT + AMP_RIGHT

        val ALL_BUT_TOP = SPEAKER + AMP
        val ALL = ALL_BUT_TOP + TOP_BAR
    }

    init {
        ledController.setLength(LENGTH)
        ledController.setData(buffer)
        ledController.start()
    }

    private const val INIT_AFTER_LOOP_CYCLE = 10
    private var loopCycles = 0

    override fun periodic() {
        if (loopCycles < INIT_AFTER_LOOP_CYCLE) {
            loopCycles++
            return
        }

        LEDState.updateBuffer()
        ledController.setData(buffer)
    }

    private val loadingNotifier = Notifier {
        synchronized(this) {
            // We need to provide a timestamp while robot code is loading
            Chase.updateBuffer(timestamp = System.currentTimeMillis() / 1000.0)
            ledController.setData(buffer)
        }
    }

    fun startLoadingThread() = loadingNotifier.startPeriodic(LOOP_PERIOD_SECS)
    fun stopLoadingThread() = loadingNotifier.stop()
}