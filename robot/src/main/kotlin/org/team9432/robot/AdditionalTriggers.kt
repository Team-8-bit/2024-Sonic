package org.team9432.robot

import edu.wpi.first.wpilibj.DriverStation
import org.team9432.lib.commandbased.commands.PrintCommand
import org.team9432.lib.commandbased.input.KTrigger
import org.team9432.robot.commands.hood.HoodAimAtSpeaker
import org.team9432.robot.subsystems.RobotPosition

object AdditionalTriggers {
    private val isAligningHood = KTrigger { (RobotPosition.distanceToSpeaker() < 5.0 && RobotState.notePosition != RobotState.NotePosition.NONE) || DriverStation.isAutonomous() }

    init {
        isAligningHood.whileTrue(HoodAimAtSpeaker())
    }
}
