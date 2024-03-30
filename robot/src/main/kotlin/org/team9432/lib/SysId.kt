package org.team9432.lib

import edu.wpi.first.units.*
import edu.wpi.first.units.Units.Seconds
import edu.wpi.first.wpilibj.sysid.SysIdRoutineLog
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction
import org.littletonrobotics.junction.Logger
import org.team9432.lib.logged.neo.LoggedNeo
import java.util.function.Consumer
import edu.wpi.first.wpilibj2.command.Command as WPICommand
import edu.wpi.first.wpilibj2.command.Subsystem as WPISubsystem

class KSysIdConfig(
    /** The voltage ramp rate used for quasistatic test routines. */
    rampRate: Measure<Velocity<Voltage>>? = null,

    /** The step voltage output used for dynamic test routines. */
    stepVoltage: Measure<Voltage>? = null,

    /** Safety timeout for the test routine commands in seconds. */
    timeout: Double? = null,
): SysIdRoutine.Config(rampRate, stepVoltage, timeout?.let { Seconds.of(it) }, { state -> Logger.recordOutput("SysIdState", state.toString()) })

class KSysIdMechanism(
    /** Sends the SysId-specified drive signal to the mechanism motors during test routines. */
    drive: Consumer<Measure<Voltage>>? = null,

    /** The name of the mechanism being tested. */
    name: String,
): SysIdRoutine.Mechanism(drive, null, object: WPISubsystem {}, name)

fun LoggedNeo.getSysIdTests(config: KSysIdConfig = KSysIdConfig()): SysIdTestContainer {
    val routine = SysIdRoutine(
        config,
        KSysIdMechanism(
            { volts -> setVoltage(volts.`in`(Units.Volts)) },
            name = this.config.deviceName
        )
    )

    return SysIdTestContainer(
        quasistaticForward = routine.quasistatic(Direction.kForward),
        quasistaticReverse = routine.quasistatic(Direction.kReverse),
        dynamicForward = routine.dynamic(Direction.kForward),
        dynamicReverse = routine.dynamic(Direction.kReverse)
    )
}

data class SysIdTestContainer(
    val quasistaticForward: WPICommand,
    val quasistaticReverse: WPICommand,
    val dynamicForward: WPICommand,
    val dynamicReverse: WPICommand,
)