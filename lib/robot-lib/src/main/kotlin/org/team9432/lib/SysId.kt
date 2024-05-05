package org.team9432.lib

import edu.wpi.first.units.Units.Seconds
import edu.wpi.first.units.Units.Volts
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction
import org.littletonrobotics.junction.Logger
import org.team9432.lib.wrappers.neo.LoggedNeo
import edu.wpi.first.wpilibj2.command.Command as WPICommand
import edu.wpi.first.wpilibj2.command.Subsystem as WPISubsystem


/** Sysid config wrapper that hides the Java unit library. */
class KSysIdConfig(
    /** The voltage ramp rate used for quasistatic test routines in volts/sec. */
    rampRate: Double? = null,

    /** The step voltage output used for dynamic test routines. */
    stepVoltage: Double? = null,

    /** Safety timeout for the test routine commands in seconds. */
    timeout: Double? = null,
): SysIdRoutine.Config(
    rampRate?.let { Volts.of(it).per(Seconds.of(1.0)) },
    stepVoltage?.let { Volts.of(it) },
    timeout?.let { Seconds.of(it) },
    { state -> Logger.recordOutput("SysIdState", state.toString()) }
)

/** Sysid mechanism wrapper that hides the Java unit library and some options we don't use. */
class KSysIdMechanism(
    /** Sends the SysId-specified drive signal to the mechanism motors during test routines. */
    drive: ((Double) -> Unit)? = null,
): SysIdRoutine.Mechanism(drive?.let { { drive(it.`in`(Volts)) } }, null, object: WPISubsystem {}, "")

object SysIdUtil {
    /** Get Sysid tests using the setVoltage() method of this motor. */
    fun LoggedNeo.getSysIdTests(config: KSysIdConfig = KSysIdConfig()) = getSysIdTests(config) { volts -> setVoltage(volts) }

    /** Get a set of Sysid tests using the given parameters. */
    fun getSysIdTests(config: KSysIdConfig = KSysIdConfig(), setMotors: (Double) -> Unit): SysIdTestContainer {
        val routine = SysIdRoutine(
            config,
            KSysIdMechanism { volts -> setMotors(volts) }
        )

        return SysIdTestContainer(
            quasistaticForward = routine.quasistatic(Direction.kForward),
            quasistaticReverse = routine.quasistatic(Direction.kReverse),
            dynamicForward = routine.dynamic(Direction.kForward),
            dynamicReverse = routine.dynamic(Direction.kReverse)
        )
    }
}

data class SysIdTestContainer(
    val quasistaticForward: WPICommand,
    val quasistaticReverse: WPICommand,
    val dynamicForward: WPICommand,
    val dynamicReverse: WPICommand,
)