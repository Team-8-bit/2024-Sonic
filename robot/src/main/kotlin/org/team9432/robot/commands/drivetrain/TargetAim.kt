package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.trajectory.TrapezoidProfile
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.util.PoseUtil.applyFlip
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotPosition
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs

class TargetAim(
    private val side: MechanismSide = MechanismSide.SPEAKER,
    private val toleranceDegrees: Double = 3.0,
    private val target: () -> Translation2d,
): KCommand() {
    override val requirements = setOf(Drivetrain)

    private var pid = ProfiledPIDController(0.06, 0.0, 0.0, TrapezoidProfile.Constraints(360.0, 360.0 * 2.0))

    init {
        pid.enableContinuousInput(-180.0, 180.0)
    }

    override fun initialize() {
        pid.reset(Gyro.getYaw().degrees)
    }

    override fun execute() {
        val currentTarget = target.invoke().applyFlip()
        Logger.recordOutput("Drive/AngleTarget", currentTarget)

        when (side) {
            MechanismSide.SPEAKER -> pid.setGoal(RobotPosition.angleTo(currentTarget).degrees)
            MechanismSide.AMP -> pid.setGoal(RobotPosition.angleTo(currentTarget).plus(Rotation2d(Math.PI)).degrees)
        }

        val rSpeed = pid.calculate(Gyro.getYaw().degrees)

        val speeds = ChassisSpeeds.fromFieldRelativeSpeeds(0.0, 0.0, rSpeed, Gyro.getYaw())
        Drivetrain.setSpeeds(speeds)
    }

    override fun isFinished() = abs(pid.goal.position - Gyro.getYaw().degrees) < toleranceDegrees
    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }
}