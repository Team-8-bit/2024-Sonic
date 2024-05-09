package org.team9432

import edu.wpi.first.hal.FRCNetComm
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.PowerDistribution
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.littletonrobotics.junction.LogFileUtil
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.littletonrobotics.junction.wpilog.WPILOGReader
import org.littletonrobotics.junction.wpilog.WPILOGWriter
import org.team9432.dashboard.lib.Dashboard
import org.team9432.dashboard.lib.widgets.dashboardButton
import org.team9432.dashboard.lib.widgets.dashboardDropdown
import org.team9432.dashboard.lib.widgets.delegates.readableDashboardDouble
import org.team9432.dashboard.lib.widgets.delegates.writableDashboardDouble
import org.team9432.dashboard.lib.widgets.delegates.writableDashboardString
import org.team9432.lib.State
import org.team9432.lib.coroutines.RIODispatcher
import org.team9432.lib.coroutines.delay
import org.team9432.lib.coroutines.rioLaunch
import org.team9432.lib.led.management.AnimationManager
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.RioLedStrip
import org.team9432.lib.robot.RobotBase
import org.team9432.lib.unit.seconds
import org.team9432.robot.Devices
import org.team9432.robot.LEDState
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoChooser
import org.team9432.robot.auto.builder.AutoBuilder
import org.team9432.robot.commands.DefaultCommands
import org.team9432.robot.oi.Controls
import org.team9432.robot.sensors.beambreaks.Beambreaks
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.sensors.vision.Vision
import org.team9432.robot.subsystems.Amp
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure
import org.team9432.robot.subsystems.drivetrain.Drivetrain

object Init {
    /** Preforms all initialization needed. */
    fun initRobot() {
        LEDStrip.create(RioLedStrip(118, Devices.LED_PORT))

        RobotBase.coroutineScope.rioLaunch {
            Dashboard.run(RIODispatcher)
        }

        RobotBase.coroutineScope.launch {
            var count by readableDashboardDouble("count", 0.0, row = 0, col = 2, rowsSpanned = 2, tab = "Testing")

            DashboardTabs.sendToDashboard()

            while (isActive) {
                delay(1.seconds)
                count++
            }
        }

        writableDashboardString("StringValue", "Initial", row = 2, col = 1, tab = "Testing") { println("Changed to $it") }
        writableDashboardDouble("Number", 2.0, row = 2, col = 2, tab = "Testing") { println("Changed number to $it") }

        dashboardButton("Test Button", row = 3, col = 0, tab = "Testing") { println("button was pressed!") }

        dashboardDropdown("Dropdown", listOf("option one", "two", "three!"), row = 3, col = 1, tab = "Testing", colsSpanned = 2) { println("$it was selected!") }

        AnimationManager
        LEDState

        initLogger()

        HAL.report(FRCNetComm.tResourceType.kResourceType_Framework, FRCNetComm.tInstances.kFramework_AdvantageKit)
        HAL.report(FRCNetComm.tResourceType.kResourceType_Language, FRCNetComm.tInstances.kLanguage_Kotlin)

        DefaultCommands.setDefaultCommands()
        Controls.setButtons()
        Vision.forwardPorts()

        Vision
        Gyro
        Beambreaks

        Amp
        Drivetrain
        Hood
        Superstructure
        Shooter

        AutoBuilder.initChoosers()
        AutoChooser.initChooser()

        RobotState.findNote()?.let { RobotState.notePosition = it }
    }

    /**
     * Initializes and starts advantagekit with some default settings, records [projectName] as metadata.
     */
    private fun initLogger() {
        Logger.recordMetadata("ProjectName", "2024-Sonic")
        Logger.recordMetadata("BuildDate", BuildConstants.BUILD_DATE)
        Logger.recordMetadata("GitSHA", BuildConstants.GIT_SHA)
        Logger.recordMetadata("GitDate", BuildConstants.GIT_DATE)
        Logger.recordMetadata("GitBranch", BuildConstants.GIT_BRANCH)
        when (BuildConstants.DIRTY) {
            0 -> Logger.recordMetadata("GitDirty", "All changes committed")
            1 -> Logger.recordMetadata("GitDirty", "Uncomitted changes")
            else -> Logger.recordMetadata("GitDirty", "Unknown")
        }

        if (RobotBase.isReal || State.mode == State.Mode.SIM) {
            Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
            Logger.addDataReceiver(NT4Publisher()) // Publish data to NetworkTables
            PowerDistribution(1, PowerDistribution.ModuleType.kRev) // Enables power distribution logging
        } else if (State.mode == State.Mode.REPLAY) {
            Robot.useTiming = false // Run as fast as possible
            val logPath = LogFileUtil.findReplayLog() // Pull the replay log from AdvantageScope (or prompt the user)
            Logger.setReplaySource(WPILOGReader(logPath)) // Read replay log
            Logger.addDataReceiver(
                WPILOGWriter(
                    LogFileUtil.addPathSuffix(
                        logPath, "_sim"
                    )
                )
            ) // Save outputs to a new log
        }

        // Logger.disableDeterministicTimestamps() // See "Deterministic Timestamps" in the "Understanding Data Flow" page
        Logger.start()
    }
}