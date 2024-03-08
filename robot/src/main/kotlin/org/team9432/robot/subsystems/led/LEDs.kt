package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.util.Color
import org.team9432.LOOP_PERIOD_SECS
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.Devices


object LEDs: KSubsystem() {
    private const val LENGTH = 88 // Two 8s!

    private val ledController = AddressableLED(Devices.LED_PORT)
    val buffer = AddressableLEDBuffer(LENGTH)
    private val loadingNotifier: Notifier

    enum class Strip(val indices: List<Int>) {
        SPEAKER_LEFT_TOP((0..11).toList()),
        SPEAKER_LEFT_BOTTOM((12..21).toList()),

        SPEAKER_RIGHT_BOTTOM((22..33).toList()),
        SPEAKER_RIGHT_TOP((34..43).toList()),

        AMP_LEFT_TOP((44..55).toList()),
        AMP_LEFT_BOTTOM((56..65).toList()),

        AMP_RIGHT_BOTTOM((66..77).toList()),
        AMP_RIGHT_TOP((78..87).toList()),

        TOP_BAR((88..100).toList()),

        SPEAKER_LEFT(SPEAKER_LEFT_BOTTOM.indices + SPEAKER_LEFT_TOP.indices),
        SPEAKER_RIGHT(SPEAKER_RIGHT_BOTTOM.indices + SPEAKER_RIGHT_TOP.indices),
        AMP_LEFT(AMP_LEFT_BOTTOM.indices + AMP_LEFT_TOP.indices),
        AMP_RIGHT(AMP_RIGHT_BOTTOM.indices + AMP_RIGHT_TOP.indices),

        TOP(SPEAKER_LEFT_BOTTOM.indices + SPEAKER_RIGHT_BOTTOM.indices + AMP_LEFT_BOTTOM.indices + AMP_RIGHT_BOTTOM.indices),
        BOTTOM(SPEAKER_LEFT_TOP.indices + SPEAKER_RIGHT_TOP.indices + AMP_LEFT_TOP.indices + AMP_RIGHT_TOP.indices),

        SPEAKER(SPEAKER_LEFT.indices + SPEAKER_RIGHT.indices),
        AMP(AMP_LEFT.indices + AMP_RIGHT.indices),

        ALL(SPEAKER.indices + AMP.indices + TOP_BAR.indices),
    }

    init {
        ledController.setLength(LENGTH)
        ledController.setData(buffer)
        ledController.start()

        loadingNotifier = Notifier {
            synchronized(this) {
                LEDModes.breath(
                    Color.kWhite,
                    Color.kBlack,
                    Strip.ALL,
                    timestamp = System.currentTimeMillis() / 1000.0
                )
                ledController.setData(buffer)
            }
        }

        loadingNotifier.startPeriodic(LOOP_PERIOD_SECS)
    }

    private const val INIT_AFTER_LOOP_CYCLE = 10
    private var loopCycles = 0

    override fun periodic() {
        if (loopCycles < INIT_AFTER_LOOP_CYCLE) {
            loopCycles++
            return
        }

        loadingNotifier.stop()

        LEDState.updateBuffer()

        ledController.setData(buffer)
    }
}