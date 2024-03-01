package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.subsystems.drivetrain.Drivetrain

class DriveToPosition(
    private val position: Pose2d,
): KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun initialize() {
        Drivetrain.setPositionGoal(position)
        Drivetrain.mode = Drivetrain.DrivetrainMode.PID
    }

    override fun isFinished(): Boolean {
        return Drivetrain.mode != Drivetrain.DrivetrainMode.PID || Drivetrain.atPositionGoal()
    }
}
