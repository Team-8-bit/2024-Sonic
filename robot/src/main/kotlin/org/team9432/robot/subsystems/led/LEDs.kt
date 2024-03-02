package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.util.Color
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.Devices
import org.team9432.robot.subsystems.beambreaks.Beambreaks
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin


object LEDs: KSubsystem() {
    private const val LENGTH = 88 // Two 8s!

    private val leds = AddressableLED(Devices.LED_PORT)
    private val buffer = AddressableLEDBuffer(LENGTH)
    private var loadingNotifier: Notifier? = null

    private const val BREATH_DURATION = 1.0
    private const val WAVE_EXPONENT = 0.4

    init {
        leds.setLength(LENGTH)
        leds.setData(buffer)
        leds.start()

        loadingNotifier = Notifier {
            synchronized(this) {
                breath(
                    Color.kWhite,
                    Color.kBlack,
                    System.currentTimeMillis() / 1000.0
                )
                leds.setData(buffer)
            }
        }.also { it.startPeriodic(0.02) }
    }

    private const val INIT_AFTER_LOOP_CYCLE = 10
    private var loopCycles = 0

    override fun periodic() {
        if (loopCycles < INIT_AFTER_LOOP_CYCLE) {
            loopCycles++
            return
        }

        loadingNotifier?.stop()

        if (DriverStation.isDisabled()) {
            breath(Color.kDarkGreen, Color.kBlack)
        } else if (DriverStation.isAutonomous()) {
            strobe(Color.kRed, 0.25)
        } else { // Teleop
            if (Beambreaks.getIntakeAmpSide() || Beambreaks.getIntakeSpeakerSide()) {
                strobe(Color.kPurple, 0.25)
            } else {
                solid(Color.kGreen)
            }
        }

        leds.setData(buffer)
    }

    private fun solid(color: Color) {
        for (i in 0 until LENGTH) {
            buffer.setLED(i, color)
        }
    }

    private fun strobe(color: Color, duration: Double) {
        val on = Timer.getFPGATimestamp() % duration / duration > 0.5
        solid(if (on) color else Color.kBlack)
    }

    private fun breath(c1: Color, c2: Color) {
        breath(c1, c2, Timer.getFPGATimestamp())
    }

    private fun breath(c1: Color, c2: Color, timestamp: Double) {
        val x = timestamp % BREATH_DURATION / BREATH_DURATION * 2.0 * Math.PI
        val ratio = (sin(x) + 1.0) / 2.0
        val red = c1.red * (1 - ratio) + c2.red * ratio
        val green = c1.green * (1 - ratio) + c2.green * ratio
        val blue = c1.blue * (1 - ratio) + c2.blue * ratio
        solid(Color(red, green, blue))
    }

    private fun rainbow(cycleLength: Double, duration: Double) {
        var x = (1 - Timer.getFPGATimestamp() / duration % 1.0) * 180.0
        val xDiffPerLed = 180.0 / cycleLength
        for (i in 0 until LENGTH) {
            x += xDiffPerLed
            x %= 180.0
            buffer.setHSV(i, x.toInt(), 255, 255)
        }
    }

    private fun wave(c1: Color, c2: Color, cycleLength: Double, duration: Double) {
        var x = (1 - Timer.getFPGATimestamp() % duration / duration) * 2.0 * Math.PI
        val xDiffPerLed = 2.0 * Math.PI / cycleLength
        for (i in 0 until LENGTH) {
            x += xDiffPerLed
            var ratio = (sin(x).pow(WAVE_EXPONENT) + 1.0) / 2.0
            if (java.lang.Double.isNaN(ratio)) {
                ratio = (-sin(x + Math.PI).pow(WAVE_EXPONENT) + 1.0) / 2.0
            }
            if (java.lang.Double.isNaN(ratio)) {
                ratio = 0.5
            }
            val red = c1.red * (1 - ratio) + c2.red * ratio
            val green = c1.green * (1 - ratio) + c2.green * ratio
            val blue = c1.blue * (1 - ratio) + c2.blue * ratio
            buffer.setLED(i, Color(red, green, blue))
        }
    }

    private fun stripes(colors: List<Color>, length: Int, duration: Double) {
        for (i in 0 until length) {
            var colorIndex = (floor((i - (Timer.getFPGATimestamp() % duration / duration * length * colors.size)) / length) + colors.size).toInt() % colors.size
            colorIndex = colors.size - 1 - colorIndex
            buffer.setLED(i, colors[colorIndex])
        }
    }
}