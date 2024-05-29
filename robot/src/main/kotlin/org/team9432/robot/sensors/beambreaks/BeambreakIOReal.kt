package org.team9432.robot.sensors.beambreaks

import edu.wpi.first.wpilibj.DigitalInput
import org.team9432.DashboardTab
import org.team9432.dashboard.lib.widgets.ReadableDashboardBooleanList
import org.team9432.dashboard.shared.WidgetPosition
import org.team9432.robot.Devices

class BeambreakIOReal: BeambreakIO {
    private val list = ReadableDashboardBooleanList(
        "Beambreaks",
        mapOf(
            "Amp Intake" to false,
            "Speaker Intake" to false,
            "Amp Hopper" to false,
            "Speaker Hopper" to false,
            "Center" to false,
        ), WidgetPosition(0, 0, DashboardTab.COMPETITION, rowsSpanned = 2, colsSpanned = 2)
    )
    private val intakeAmpSide = DigitalInput(Devices.INTAKE_AMP_SIDE_BEAMBREAK_PORT)
    private val intakeSpeakerSide = DigitalInput(Devices.INTAKE_SPEAKER_SIDE_BEAMBREAK_PORT)
    private val hopperAmpSide = DigitalInput(Devices.HOPPER_AMP_SIDE_BEAMBREAK_PORT)
    private val hopperSpeakerSide = DigitalInput(Devices.HOPPER_SPEAKER_SIDE_BEAMBREAK_PORT)
    private val center = DigitalInput(Devices.CENTER_BEAMBREAK_PORT)

    override fun updateInputs(inputs: BeambreakIO.BeambreakIOInputs) {
        inputs.intakeAmpSideActive = intakeAmpSide.get()
        inputs.intakeSpeakerSideActive = intakeSpeakerSide.get()
        inputs.hopperAmpSideActive = hopperAmpSide.get()
        inputs.hopperSpeakerSideActive = hopperSpeakerSide.get()
        inputs.centerActive = center.get()

        list["Amp Intake"] = inputs.intakeAmpSideActive
        list["Speaker Intake"] = inputs.intakeSpeakerSideActive
        list["Amp Hopper"] = inputs.hopperAmpSideActive
        list["Speaker Hopper"] = inputs.hopperSpeakerSideActive
        list["Center"] = inputs.centerActive
    }
}