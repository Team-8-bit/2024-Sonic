package org.team9432.robot.subsystems.led

import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.util.Color
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.Devices
import org.team9432.robot.RobotState
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin


object LEDs : KSubsystem() {
    private const val LENGTH = 88 // Two 8s!

    private val leds = AddressableLED(Devices.LED_PORT)
    private val buffer = AddressableLEDBuffer(LENGTH)
    private var loadingNotifier: Notifier? = null

    private const val BREATH_DURATION = 3.0
    private const val WAVE_EXPONENT = 0.4

    private val MAIN_GREEN = Color(0.05, 1.0, 0.1)

    fun setBuffer(strip: Strip, index: Int, color: Color) {
        buffer.setLED(getIndex(strip, index), color)
    }

    fun setBufferHSV(strip: Strip, index: Int, h: Int, s: Int, v: Int) {
        buffer.setHSV(getIndex(strip, index), h, s, v)
    }

    fun getIndex(strip: Strip, index: Int): Int {
        return when (strip) {
            Strip.FRONT_LEFT -> index
            Strip.FRONT_RIGHT -> 22 + index
            Strip.BACK_RIGHT -> 44 + index
            Strip.BACK_LEFT -> 66 + index
            Strip.ALL -> index
        }
    }

    enum class Strip(val length: Int) {
        FRONT_LEFT(22), FRONT_RIGHT(22), BACK_RIGHT(22), BACK_LEFT(22), ALL(88)
    }

    init {
        leds.setLength(LENGTH)
        leds.setData(buffer)
        leds.start()

        loadingNotifier = Notifier {
            synchronized(this) {
                breath(
                    Color.kWhite,
                    Color.kBlack,
                    System.currentTimeMillis() / 1000.0,
                    Strip.ALL
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
            breath(MAIN_GREEN, Color.kBlack, Strip.ALL)
        } else if (DriverStation.isAutonomous()) {
            strobe(Color.kRed, 0.25, Strip.ALL)
        } else if (DriverStation.isTest()) {
            solid(Color.kRed, Strip.FRONT_RIGHT)
            solid(Color.kBlue, Strip.FRONT_LEFT)
            solid(Color.kGreen, Strip.BACK_RIGHT)
            solid(Color.kYellow, Strip.BACK_LEFT)
        } else { // Teleop
            if (RobotState.noteInAmpSideIntakeBeambreak()) {
                strobe(Color.kPurple, 0.25, Strip.BACK_RIGHT)
                strobe(Color.kPurple, 0.25, Strip.BACK_LEFT)
            } else if (RobotState.noteInSpeakerSideIntakeBeambreak()) {
                strobe(Color.kPurple, 0.25, Strip.FRONT_RIGHT)
                strobe(Color.kPurple, 0.25, Strip.FRONT_LEFT)
            } else {
                rainbow(30.0, 0.5, Strip.ALL)
            }
        }

        leds.setData(buffer)
    }

    private fun solid(color: Color, strip: Strip) {
        for (i in 0 until strip.length) {
            setBuffer(strip, i, color)
        }
    }

    private fun strobe(color: Color, duration: Double, strip: Strip) {
        val on = Timer.getFPGATimestamp() % duration / duration > 0.5
        solid(if (on) color else Color.kBlack, strip)
    }

    private fun breath(c1: Color, c2: Color, strip: Strip) {
        breath(c1, c2, Timer.getFPGATimestamp(), strip)
    }

    private fun breath(c1: Color, c2: Color, timestamp: Double, strip: Strip) {
        val x = timestamp % BREATH_DURATION / BREATH_DURATION * 2.0 * Math.PI
        val ratio = (sin(x) + 1.0) / 2.0
        val red = c1.red * (1 - ratio) + c2.red * ratio
        val green = c1.green * (1 - ratio) + c2.green * ratio
        val blue = c1.blue * (1 - ratio) + c2.blue * ratio
        solid(Color(red, green, blue), strip)
    }

    private fun rainbow(cycleLength: Double, duration: Double, strip: Strip) {
        var x = (1 - Timer.getFPGATimestamp() / duration % 1.0) * 180.0
        val xDiffPerLed = 180.0 / cycleLength
        for (i in 0 until strip.length) {
            x += xDiffPerLed
            x %= 180.0
            setBufferHSV(strip, i, x.toInt(), 255, 255)
        }
    }

    private fun wave(c1: Color, c2: Color, cycleLength: Double, duration: Double, strip: Strip) {
        var x = (1 - Timer.getFPGATimestamp() % duration / duration) * 2.0 * Math.PI
        val xDiffPerLed = 2.0 * Math.PI / cycleLength
        for (i in 0 until strip.length) {
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
            setBuffer(strip, i, Color(red, green, blue))
        }
    }

    private fun stripes(colors: List<Color>, length: Int, duration: Double, strip: Strip) {
        for (i in 0 until length) {
            var colorIndex = (floor((i - (Timer.getFPGATimestamp() % duration / duration * length * colors.size)) / length) + colors.size).toInt() % colors.size
            colorIndex = colors.size - 1 - colorIndex
            setBuffer(strip, i, colors[colorIndex])
        }
    }
}