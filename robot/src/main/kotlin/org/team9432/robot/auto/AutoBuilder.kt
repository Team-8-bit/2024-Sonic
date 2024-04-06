package org.team9432.robot.auto

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.commands.CollectPreload
import org.team9432.robot.auto.commands.ExitAuto
import org.team9432.robot.auto.commands.InitAuto
import org.team9432.robot.auto.subsections.ScoreNote
import org.team9432.robot.auto.subsections.StartNote

object AutoBuilder {
    private val initChooser = LoggedDashboardChooser<() -> KCommand>("Starting Rotation")
    private val firstChooser = LoggedDashboardChooser<() -> KCommand>("Start Note")
    private val secondChooser = LoggedDashboardChooser<() -> KCommand>("Second Note")
    private val thirdChooser = LoggedDashboardChooser<() -> KCommand>("Third Note")

    fun initChoosers() {
        initChooser.initInit()
        firstChooser.initStart()
        secondChooser.initStep()
        thirdChooser.initStep()
    }

    fun getAuto() = SequentialCommand(
        initChooser.get().invoke(),
        CollectPreload(),
        firstChooser.get().invoke(),
        secondChooser.get().invoke(),
        thirdChooser.get().invoke(),
        ExitAuto()
    )

    fun getInitCommand(): KCommand {
        return initChooser.get().invoke()
    }

    private fun LoggedDashboardChooser<() -> KCommand>.initStart() {
        addDefaultOption("None") { InstantCommand {} }
        addOption("Amp Note") { StartNote(AllianceNote.AMP) }
        addOption("Center Note") { StartNote(AllianceNote.CENTER) }
        addOption("Stage Note") { StartNote(AllianceNote.STAGE) }
    }

    private fun LoggedDashboardChooser<() -> KCommand>.initStep() {
        addDefaultOption("None") { InstantCommand {} }
        addOption("Amp Note") { ScoreNote(AllianceNote.AMP) }
        addOption("Center Note") { ScoreNote(AllianceNote.CENTER) }
        addOption("Stage Note") { ScoreNote(AllianceNote.STAGE) }
    }

    private fun LoggedDashboardChooser<() -> KCommand>.initInit() {
        addDefaultOption("Backwards") { InitAuto(Rotation2d(Math.PI)) }
        addOption("Forwards") { InitAuto(Rotation2d()) }
    }
}