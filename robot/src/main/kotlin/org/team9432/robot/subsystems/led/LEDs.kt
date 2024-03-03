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

    enum class Strip(val indices: List<Int>) {
        FRONT_LEFT((0..21).toList()),
        FRONT_RIGHT((22..43).toList()),
        BACK_RIGHT((44..65).toList()),
        BACK_LEFT((66..87).toList()),
        ALL((0..87).toList())
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
                    Strip.ALL,
                    timestamp = System.currentTimeMillis() / 1000.0
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
            breath(MAIN_GREEN, Color.kBlack, Strip.ALL, 3.0)
        } else if (DriverStation.isAutonomous()) {
            strobe(Color.kRed, 0.25, Strip.ALL)
        } else if (DriverStation.isTest()) {
            solid(Color.kRed, Strip.FRONT_RIGHT)
            solid(Color.kBlue, Strip.FRONT_LEFT)
            solid(Color.kGreen, Strip.BACK_RIGHT)
            solid(Color.kYellow, Strip.BACK_LEFT)
        } else { // Teleop
            rainbow(30.0, 0.5, Strip.ALL) // This will be the default unless overwritten later

            if (RobotState.noteInAmpSideIntakeBeambreak()) {
                strobe(Color.kPurple, 0.25, Strip.FRONT_LEFT)
                strobe(Color.kPurple, 0.25, Strip.FRONT_RIGHT)
            } else if (RobotState.noteInSpeakerSideIntakeBeambreak()) {
                strobe(Color.kPurple, 0.25, Strip.BACK_LEFT)
                strobe(Color.kPurple, 0.25, Strip.BACK_RIGHT)
            }
        }

        leds.setData(buffer)
    }

    private fun solid(color: Color, strip: Strip) {
        for (index in strip.indices) {
            buffer.setLED(index, color)
        }
    }

    private fun strobe(color: Color, duration: Double, strip: Strip) {
        val on = Timer.getFPGATimestamp() % duration / duration > 0.5
        solid(if (on) color else Color.kBlack, strip)
    }

    private fun breath(c1: Color, c2: Color, strip: Strip, duration: Double = 1.0, timestamp: Double = Timer.getFPGATimestamp()) {
        val x = timestamp % duration / duration * 2.0 * Math.PI
        val ratio = (sin(x) + 1.0) / 2.0
        val red = c1.red * (1 - ratio) + c2.red * ratio
        val green = c1.green * (1 - ratio) + c2.green * ratio
        val blue = c1.blue * (1 - ratio) + c2.blue * ratio
        solid(Color(red, green, blue), strip)
    }

    private fun rainbow(cycleLength: Double, duration: Double, strip: Strip) {
        var x = (1 - Timer.getFPGATimestamp() / duration % 1.0) * 180.0
        val xDiffPerLed = 180.0 / cycleLength
        for (index in strip.indices) {
            x += xDiffPerLed
            x %= 180.0
            buffer.setHSV(index, x.toInt(), 255, 255)
        }
    }

    private fun wave(c1: Color, c2: Color, cycleLength: Double, duration: Double, strip: Strip) {
        var x = (1 - Timer.getFPGATimestamp() % duration / duration) * 2.0 * Math.PI
        val xDiffPerLed = 2.0 * Math.PI / cycleLength
        for (index in strip.indices) {
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
            buffer.setLED(index, Color(red, green, blue))
        }
    }

    private fun stripes(colors: List<Color>, length: Int, duration: Double, strip: Strip) {
        for (index in strip.indices) {
            var colorIndex = (floor((index - (Timer.getFPGATimestamp() % duration / duration * length * colors.size)) / length) + colors.size).toInt() % colors.size
            colorIndex = colors.size - 1 - colorIndex
            buffer.setLED(index, colors[colorIndex])
        }
    }
}