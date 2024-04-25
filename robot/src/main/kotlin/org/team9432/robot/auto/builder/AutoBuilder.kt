package org.team9432.robot.auto.builder

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.littletonrobotics.junction.networktables.LoggedDashboardNumber
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.auto.SpikeNote
import org.team9432.robot.auto.autos.InitAuto
import org.team9432.robot.auto.builder.subsections.ScoreNote
import org.team9432.robot.auto.builder.subsections.StartNote
import org.team9432.robot.commands.hopper.PullFromSpeakerShooter
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

    /** Returns an auto built on the values from each chooser. */
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

    fun getInitCommand() = initChooser.get().invoke()

    /** Initializes a [LoggedDashboardChooser] with options for the first note in auto. */
    private fun LoggedDashboardChooser<() -> KCommand>.initStart() {
        addDefaultOption("None") { InstantCommand {} }
        addOption("Amp Note") { StartNote(SpikeNote.AMP) }
        addOption("Center Note") { StartNote(SpikeNote.CENTER) }
        addOption("Stage Note") { StartNote(SpikeNote.STAGE) }
    }

    /** Initializes a [LoggedDashboardChooser] with options for notes after the first in auto. */
    private fun LoggedDashboardChooser<() -> KCommand>.initStep() {
        addDefaultOption("None") { InstantCommand {} }
        addOption("Amp Note") { ScoreNote(SpikeNote.AMP) }
        addOption("Center Note") { ScoreNote(SpikeNote.CENTER) }
        addOption("Stage Note") { ScoreNote(SpikeNote.STAGE) }
    }

    /** Initializes a [LoggedDashboardChooser] with options to initialize the robot. */
    private fun LoggedDashboardChooser<() -> KCommand>.initInit() {
        addDefaultOption("Backwards") { InitAuto(Rotation2d(Math.PI)) }
        addOption("Forwards") { InitAuto(Rotation2d()) }
    }
}