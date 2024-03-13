package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.subsystems.drivetrain.Drivetrain

class DriveToPosition(
    private val position: Pose2d,
    private val positionalTolerance: Double = Drivetrain.POSITIONAL_TOLERANCE,
    private val rotationalTolerance: Double = Drivetrain.ROTATIONAL_TOLERANCE,
): KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun initialize() {
        Drivetrain.setPositionGoal(position)
    }

    override fun execute() {
        Drivetrain.setSpeeds(Drivetrain.calculatePositionSpeed())
    }

    override fun isFinished(): Boolean {
        return Drivetrain.atPositionGoal(positionalTolerance, rotationalTolerance)
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }
}

