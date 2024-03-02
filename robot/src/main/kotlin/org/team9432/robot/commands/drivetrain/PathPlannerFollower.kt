package org.team9432.robot.commands.drivetrain

import com.pathplanner.lib.path.PathPlannerTrajectory
import edu.wpi.first.wpilibj.DriverStation.Alliance
import edu.wpi.first.wpilibj.Timer
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.util.PoseUtil
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.drivetrain.Drivetrain.DrivetrainMode

class PathPlannerFollower(private val trajectory: PathPlannerTrajectory, private val allowFlip: Boolean = true): KCommand() {
    private val trajectoryTimer = Timer()

    override val requirements: MutableSet<KSubsystem> = mutableSetOf(Drivetrain)

    override fun initialize() {
        trajectoryTimer.reset()
        trajectoryTimer.start()

        // Display the trajectory in advantagescope, but remove a bunch of states to reduce lag
        Logger.recordOutput("CurrentTrajectory", *trajectory.states.map { it.targetHolonomicPose }.filterIndexed { index, _ -> index % 1 /* <- Increase this number to reduce states */ == 0 }.toTypedArray())

        Drivetrain.mode = DrivetrainMode.PID
    }

    override fun execute() {
        val state = trajectory.sample(trajectoryTimer.get())
        if (Robot.alliance == Alliance.Blue || !allowFlip) {
            Drivetrain.setPositionGoal(state.targetHolonomicPose)
        } else {
            Drivetrain.setPositionGoal(PoseUtil.flip(state.targetHolonomicPose))
        }
    }

    override fun isFinished(): Boolean {
        if (Drivetrain.mode != DrivetrainMode.PID) return true

        return if (Robot.alliance == Alliance.Blue || !allowFlip) {
            Drivetrain.isNear(trajectory.endState.targetHolonomicPose, 0.1)
        } else {
            Drivetrain.isNear(PoseUtil.flip(trajectory.endState.targetHolonomicPose), 0.1)
        }
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }
}
