package org.team9432.robot.subsystems.intake

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.SparkPIDController
import edu.wpi.first.wpilibj.DigitalInput
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.Devices

class IntakeIONeo: IntakeIO {
    private val ampSide = KSparkMAX(Devices.AMP_SIDE_INTAKE_ID)
    private val speakerSide = KSparkMAX(Devices.SPEAKER_SIDE_INTAKE_ID)

    private val ampSideBeambreak = DigitalInput(Devices.INTAKE_AMP_SIDE_BEAMBREAK_PORT)
    private val speakerSideBeambreak = DigitalInput(Devices.INTAKE_SHOOTER_SIDE_BEAMBREAK_PORT)
    private val centerBeambreak = DigitalInput(Devices.INTAKE_CENTER_BEAMBREAK_PORT)

    private val ampSideEncoder = ampSide.encoder
    private val ampSidePid = ampSide.pidController

    private val speakerSideEncoder = speakerSide.encoder
    private val speakerSidePid = speakerSide.pidController

    private val gearRatio = 2

    init {
        ampSide.restoreFactoryDefaults()
        speakerSide.restoreFactoryDefaults()

        ampSide.inverted = false
        speakerSide.inverted = true

        ampSide.idleMode = IdleMode.kCoast
        speakerSide.idleMode = IdleMode.kCoast

        ampSide.enableVoltageCompensation(12.0)
        speakerSide.enableVoltageCompensation(12.0)

        ampSide.setSmartCurrentLimit(20)
        speakerSide.setSmartCurrentLimit(20)

        ampSide.burnFlash()
        speakerSide.burnFlash()
    }

    override fun updateInputs(inputs: IntakeIO.IntakeIOInputs) {
        inputs.ampSideVelocityRPM = ampSideEncoder.velocity / gearRatio
        inputs.ampSideAppliedVolts = ampSide.appliedOutput * ampSide.busVoltage
        inputs.ampSideCurrentAmps = ampSide.outputCurrent
        inputs.ampSideBeambreakActive = ampSideBeambreak.get()

        inputs.speakerSideVelocityRPM = speakerSideEncoder.velocity / gearRatio
        inputs.speakerSideAppliedVolts = speakerSide.appliedOutput * speakerSide.busVoltage
        inputs.speakerSideCurrentAmps = speakerSide.outputCurrent
        inputs.speakerSideBeambreakActive = speakerSideBeambreak.get()

        inputs.centerBeambreakActive = centerBeambreak.get()
    }

    override fun setVoltage(ampSideVolts: Double, speakerSideVolts: Double) {
        ampSide.setVoltage(ampSideVolts)
        speakerSide.setVoltage(speakerSideVolts)
    }

    override fun setSpeed(ampSideRPM: Double, ampSideFFVolts: Double, speakerSideRPM: Double, speakerSideFFVolts: Double) {
        ampSidePid.setReference(
            ampSideRPM * gearRatio,
            CANSparkBase.ControlType.kVelocity,
            0, // PID slot
            ampSideFFVolts,
            SparkPIDController.ArbFFUnits.kVoltage
        )
        speakerSidePid.setReference(
            speakerSideRPM * gearRatio,
            CANSparkBase.ControlType.kVelocity,
            0, // PID slot
            speakerSideFFVolts,
            SparkPIDController.ArbFFUnits.kVoltage
        )
    }

    override fun setPID(p: Double, i: Double, d: Double) {
        ampSidePid.setP(p, 0)
        ampSidePid.setI(i, 0)
        ampSidePid.setD(d, 0)
        ampSidePid.setFF(0.0, 0)
        speakerSidePid.setP(p, 0)
        speakerSidePid.setI(i, 0)
        speakerSidePid.setD(d, 0)
        speakerSidePid.setFF(0.0, 0)
    }

    override fun stop() {
        ampSide.stopMotor()
        speakerSide.stopMotor()
    }
}