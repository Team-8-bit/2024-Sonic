package org.team9432.robot.auto

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.littletonrobotics.junction.networktables.LoggedDashboardNumber
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.auto.commands.InitAuto
import org.team9432.robot.auto.commands.PullFromSpeakerShooter
import org.team9432.robot.auto.subsections.ScoreNote
import org.team9432.robot.auto.subsections.StartNote
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter

object AutoBuilder {
    private val initChooser = LoggedDashboardChooser<() -> KCommand>("Starting Rotation")
    private val firstDelay = LoggedDashboardNumber("First Delay")
    private val firstChooser = LoggedDashboardChooser<() -> KCommand>("Start Note")
    private val secondDelay = LoggedDashboardNumber("Second Delay")
    private val secondChooser = LoggedDashboardChooser<() -> KCommand>("Second Note")
    private val thirdDelay = LoggedDashboardNumber("Third Delay")
    private val thirdChooser = LoggedDashboardChooser<() -> KCommand>("Third Note")

    fun initChoosers() {
        initChooser.initInit()
        firstChooser.initStart()
        secondChooser.initStep()
        thirdChooser.initStep()
    }

    fun getAuto() = SequentialCommand(
        initChooser.get().invoke(),
        PullFromSpeakerShooter(),
        ParallelDeadlineCommand(
            Hood.Commands.aimAtSpeaker(),
            Shooter.Commands.runAtSpeeds(),

            deadline = SequentialCommand(
                WaitCommand(firstDelay.get()),
                firstChooser.get().invoke(),
                WaitCommand(secondDelay.get()),
                secondChooser.get().invoke(),
                WaitCommand(thirdDelay.get()),
                thirdChooser.get().invoke()
            )
        )
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