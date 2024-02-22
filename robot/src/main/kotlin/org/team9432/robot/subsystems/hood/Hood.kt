package org.team9432.robot.subsystems.hood

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object Hood: KSubsystem() {
    private val inputs = LoggedHoodIOInputs()
    private val io = when (Robot.mode) {
        REAL, REPLAY -> HoodIONeo()
        SIM -> TODO()
    }

    private var currentAngleTarget = 0.0
        set(value) {
            io.setAngle(value)
        }

    override fun constantPeriodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Hood", inputs)
        
    }
}