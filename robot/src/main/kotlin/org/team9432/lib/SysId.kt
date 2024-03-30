package org.team9432.lib

import edu.wpi.first.units.Measure
import edu.wpi.first.units.Time
import edu.wpi.first.units.Velocity
import edu.wpi.first.units.Voltage
import edu.wpi.first.wpilibj.sysid.SysIdRoutineLog
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import java.util.function.Consumer
import edu.wpi.first.wpilibj2.command.Subsystem as WPISubsystem

class KSysIdConfig(
    /** The voltage ramp rate used for quasistatic test routines. */
    rampRate: Measure<Velocity<Voltage>>? = null,

    /** The step voltage output used for dynamic test routines. */
    stepVoltage: Measure<Voltage>? = null,

    /** Safety timeout for the test routine commands. */
    timeout: Measure<Time>? = null,

    /** Optional handle for recording test state in a third-party logging solution. */
    recordState: Consumer<SysIdRoutineLog.State>? = null,
): SysIdRoutine.Config(rampRate, stepVoltage, timeout, recordState)

class KSysIdMechanism(
    /** Sends the SysId-specified drive signal to the mechanism motors during test routines. */
    drive: Consumer<Measure<Voltage>>? = null,

    /** Returns measured data (voltages, positions, velocities) of the mechanism motors during test routines. */
    log: Consumer<SysIdRoutineLog>? = null,

    /** The name of the mechanism being tested. */
    name: String,
): SysIdRoutine.Mechanism(drive, log, object: WPISubsystem {}, name)