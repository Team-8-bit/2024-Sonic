package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.logged.neo.LoggedNeo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import kotlin.math.abs

object Superstructure: KSubsystem() {
    private val ampSide = LoggedNeo(getIntakeConfig(Devices.AMP_SIDE_INTAKE_ID, true, "Amp"))
    private val speakerSide = LoggedNeo(getIntakeConfig(Devices.SPEAKER_SIDE_INTAKE_ID, false, "Speaker"))
    private val hopper = LoggedNeo(getHopperConfig())

    private fun setIntakeVoltage(ampVolts: Double, speakerVolts: Double) {
        ampSide.setVoltage(ampVolts)
        speakerSide.setVoltage(speakerVolts)
    }

    fun intake(ampVolts: Double, speakerVolts: Double) = setIntakeVoltage(abs(ampVolts), abs(speakerVolts))
    fun outtake(ampVolts: Double, speakerVolts: Double) = setIntakeVoltage(-abs(ampVolts), -abs(speakerVolts))

    fun intakeSide(side: MechanismSide, volts: Double) = when (side) {
        MechanismSide.AMP -> intake(volts, 0.0)
        MechanismSide.SPEAKER -> intake(0.0, volts)
    }

    fun outtakeSide(side: MechanismSide, volts: Double) = when (side) {
        MechanismSide.AMP -> outtake(volts, 0.0)
        MechanismSide.SPEAKER -> outtake(0.0, volts)
    }

    fun stop() {
        ampSide.stop()
        speakerSide.stop()
        hopper.stop()
    }

    fun runTeleIntake(volts: Double) {
        val absVolts = abs(volts)
        if (RobotState.shouldRunOneIntake()) {
            when (RobotState.getMovementDirection()) {
                MechanismSide.SPEAKER -> intake(0.0, absVolts)
                MechanismSide.AMP -> intake(absVolts, 0.0)
            }
        } else intake(absVolts, absVolts)
    }

    fun setHopperVoltage(volts: Double) {
        hopper.setVoltage(volts)
    }

    fun loadToHopper(side: MechanismSide, volts: Double) = if (side == MechanismSide.SPEAKER) setHopperVoltage(-volts) else setHopperVoltage(volts)
    fun unloadFromHopper(side: MechanismSide, volts: Double) = if (side == MechanismSide.SPEAKER) setHopperVoltage(volts) else setHopperVoltage(-volts)

    fun shootSide(side: MechanismSide, volts: Double = 5.0) {
        loadToHopper(side, volts)
        intakeSide(side, volts)
    }

    fun unloadSide(side: MechanismSide, volts: Double = 2.0) {
        unloadFromHopper(side, volts)
        outtakeSide(side, volts)
    }

    override fun periodic() {
        ampSide.updateAndRecordInputs()
        speakerSide.updateAndRecordInputs()
        hopper.updateAndRecordInputs()
    }

    object Commands {
        fun startIntake(ampVolts: Double, speakerVolts: Double) = InstantCommand(Superstructure) { intake(ampVolts, speakerVolts) }
        fun startOuttake(ampVolts: Double, speakerVolts: Double) = InstantCommand(Superstructure) { outtake(ampVolts, speakerVolts) }
        fun startTeleIntake(volts: Double) = InstantCommand(Superstructure) { Superstructure.runTeleIntake(volts) }
        fun startIntakeSide(side: MechanismSide, volts: Double) = InstantCommand(Superstructure) { intakeSide(side, volts) }
        fun startOuttakeSide(side: MechanismSide, volts: Double) = InstantCommand(Superstructure) { outtakeSide(side, volts) }

        fun runShootSide(side: MechanismSide, volts: Double = 5.0) = SimpleCommand(
            requirements = setOf(Superstructure),
            execute = { shootSide(side, volts) },
            end = { Superstructure.stop() }
        )

        fun runUnloadSide(side: MechanismSide, volts: Double = 2.0) = SimpleCommand(
            requirements = setOf(Superstructure),
            execute = { unloadSide(side, volts) },
            end = { Superstructure.stop() }
        )

        fun runTeleIntake(volts: Double) = SimpleCommand(
            requirements = setOf(Superstructure),
            execute = { Superstructure.runTeleIntake(volts) },
            end = { Superstructure.stop() }
        )

        fun runIntakeSide(side: MechanismSide, volts: Double) = SimpleCommand(
            requirements = setOf(Superstructure),
            execute = { intakeSide(side, volts) },
            end = { Superstructure.stop() }
        )

        fun runOuttakeSide(side: MechanismSide, volts: Double) = SimpleCommand(
            requirements = setOf(Superstructure),
            execute = { outtakeSide(side, volts) },
            end = { Superstructure.stop() }
        )

        fun runOuttake() = SimpleCommand(
            requirements = setOf(Superstructure),
            initialize = { outtake(8.0, 8.0) },
            end = {
                Superstructure.stop()
                if (!RobotState.noteInAnyIntake()) RobotState.notePosition = RobotState.NotePosition.NONE
            }
        )

        fun setHopperVoltage(volts: Double) = InstantCommand(Superstructure) { Superstructure.setHopperVoltage(volts) }
        fun stop() = InstantCommand(Superstructure) { Superstructure.stop() }

        fun startLoadToHopper(side: MechanismSide, volts: Double) = InstantCommand(Superstructure) { loadToHopper(side, volts) }
        fun startUnloadFromHopper(side: MechanismSide, volts: Double) = InstantCommand(Superstructure) { unloadFromHopper(side, volts) }

        fun runLoadToHopper(side: MechanismSide, volts: Double) = SimpleCommand(
            requirements = setOf(Superstructure),
            execute = { loadToHopper(side, volts) },
            end = { Superstructure.stop() }
        )

        fun runUnloadFromHopper(side: MechanismSide, volts: Double) = SimpleCommand(
            requirements = setOf(Superstructure),
            execute = { unloadFromHopper(side, volts) },
            end = { Superstructure.stop() }
        )
    }

    private fun getIntakeConfig(canID: Int, inverted: Boolean, side: String): LoggedNeo.Config {
        return LoggedNeo.Config(
            canID = canID,
            motorType = Spark.MotorType.NEO,
            deviceName = "$side Side Intake",
            sparkConfig = Spark.Config(
                inverted = inverted,
                idleMode = CANSparkBase.IdleMode.kCoast,
                stallCurrentLimit = 80
            ),
            logName = "Intake",
            additionalQualifier = side,
            gearRatio = 2.0,
            simJkgMetersSquared = 0.003
        )
    }

    private fun getHopperConfig() = LoggedNeo.Config(
        canID = Devices.HOPPER_ID,
        motorType = Spark.MotorType.NEO,
        deviceName = "Hopper Motor",
        logName = "Hopper",
        gearRatio = 1.0,
        simJkgMetersSquared = 0.0015,
        sparkConfig = Spark.Config(
            inverted = true,
            idleMode = CANSparkBase.IdleMode.kBrake,
            stallCurrentLimit = 60
        )
    )
}