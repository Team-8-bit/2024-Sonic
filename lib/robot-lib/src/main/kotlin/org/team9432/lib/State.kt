package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.robot.RobotBase
import kotlin.jvm.optionals.getOrNull

object State: KPeriodic() {
    /** The alliance that the robot is on according to the driver station. Null if not connected. */
    var alliance: DriverStation.Alliance? = null
        private set

    /** The mode the robot is currently running in, always uses real when running on the robot. */
    val mode = if (RobotBase.isReal) Mode.REAL else Mode.SIM

    override fun periodic() {
        DriverStation.getAlliance().getOrNull()?.let { alliance = it }
    }

    enum class Mode {
        REAL, SIM, REPLAY
    }
}