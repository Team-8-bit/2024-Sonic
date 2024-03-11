package org.team9432.robot.auto

import org.team9432.robot.auto.subsections.ScoreNote
import org.team9432.robot.auto.subsections.StartNote

fun ScoreAmpNote() = ScoreNote(AutoConstants.ampNoteIntakePose)
fun ScoreCenterNote() = ScoreNote(AutoConstants.centerNoteIntakePose)
fun ScoreStageNote() = ScoreNote(AutoConstants.stageNoteIntakePose)

fun StartAmpNote() = StartNote(AutoConstants.ampNoteIntakePose)
fun StartCenterNote() = StartNote(AutoConstants.centerNoteIntakePose)
fun StartStageNote() = StartNote(AutoConstants.stageNoteIntakePose)