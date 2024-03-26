package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation
import org.littletonrobotics.junction.LoggedRobot
import org.team9432.lib.commandbased.KPeriodic
import kotlin.jvm.optionals.getOrNull

object State: KPeriodic() {
    var alliance: DriverStation.Alliance? = null
        private set

    val mode = if (LoggedRobot.isReal()) Mode.REAL else Mode.SIM

    override fun periodic() {
        DriverStation.getAlliance().getOrNull()?.let { alliance = it }
    }

    enum class Mode {
        REAL, SIM, REPLAY
    }
}