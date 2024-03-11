package org.team9432.robot.auto

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.subsections.ScoreNote
import org.team9432.robot.auto.subsections.StartNote

object AutoBuilder {
    private val initChooser = SendableChooser<KCommand>()
    private val firstChooser = SendableChooser<KCommand>()
    private val secondChooser = SendableChooser<KCommand>()
    private val thirdChooser = SendableChooser<KCommand>()
    private val fourthChooser = SendableChooser<KCommand>()

    init {
        initChooser.initInit()
        firstChooser.initStart()
        secondChooser.initStep()
        thirdChooser.initStep()
        fourthChooser.initStep()
    }

    fun getAuto() = SequentialCommand(
        initChooser.selected,
        firstChooser.selected,
        secondChooser.selected,
        thirdChooser.selected,
        fourthChooser.selected
    )

    fun initDashboard() {
        LoggedDashboardChooser("Starting Rotation", initChooser)
        LoggedDashboardChooser("Start Note", firstChooser)
        LoggedDashboardChooser("Second Note", secondChooser)
        LoggedDashboardChooser("Third Note", thirdChooser)
        LoggedDashboardChooser("Fourth Note", fourthChooser)
    }

    private fun SendableChooser<KCommand>.initStart() {
        setDefaultOption("None", InstantCommand {})
        addOption("Amp Note", StartNote(AllianceNote.AMP))
        addOption("Center Note", StartNote(AllianceNote.CENTER))
        addOption("Stage Note", StartNote(AllianceNote.STAGE))
    }

    private fun SendableChooser<KCommand>.initStep() {
        setDefaultOption("None", InstantCommand {})
        addOption("Amp Note", ScoreNote(AllianceNote.AMP))
        addOption("Center Note", ScoreNote(AllianceNote.CENTER))
        addOption("Stage Note", ScoreNote(AllianceNote.STAGE))
    }

    private fun SendableChooser<KCommand>.initInit() {
        setDefaultOption("Backwards", InitAuto(Rotation2d(Math.PI)))
        addOption("Forwards", InitAuto(Rotation2d()))
    }
}