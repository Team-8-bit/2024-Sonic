package org.team9432.robot.sensors.vision

import edu.wpi.first.net.PortForwarder
import org.littletonrobotics.junction.Logger
import org.team9432.DashboardTab
import org.team9432.dashboard.lib.widgets.delegates.readableDashboardBoolean
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.commandbased.KPeriodic

object Vision: KPeriodic() {
    private val io: VisionIO
    private val inputs = LoggedVisionIOInputs()

    private var dashboardVisionConnected by readableDashboardBoolean("Vision Connected", false, row = 2, col = 0, DashboardTab.COMPETITION)
    private var dashboardVisionHasTarget by readableDashboardBoolean("Vision Has Target", false, row = 2, col = 1, DashboardTab.COMPETITION)

    init {
        io = when (State.mode) {
            REAL, REPLAY -> VisionIOPhotonvision()
            SIM -> object: VisionIO {}
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        dashboardVisionConnected = inputs.connected
        dashboardVisionHasTarget = inputs.trackedTags.isNotEmpty()
        Logger.processInputs("Vision", inputs)
    }

    fun hasVisionTarget() = inputs.trackedTags.isNotEmpty()

    val connected get() = inputs.connected

    fun forwardPorts() {
        PortForwarder.add(5800, "10.93.32.11", 5800)
        PortForwarder.add(5800, "10.93.32.12", 5800)
    }
}