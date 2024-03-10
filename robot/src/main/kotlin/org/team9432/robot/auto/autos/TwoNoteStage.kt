package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.auto.InitAuto
import org.team9432.robot.auto.subsections.StartStageNote

fun TwoNoteStage() = SequentialCommand(
    InitAuto(Rotation2d(Math.PI)),
    StartStageNote()
)