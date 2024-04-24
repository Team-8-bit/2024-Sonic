package org.team9432.lib.advantagekit

import edu.wpi.first.wpilibj.PowerDistribution
import org.littletonrobotics.junction.LogFileUtil
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.littletonrobotics.junction.wpilog.WPILOGReader
import org.littletonrobotics.junction.wpilog.WPILOGWriter
import org.team9432.BuildConstants
import org.team9432.Robot
import org.team9432.lib.State
import org.team9432.lib.coroutineshims.RobotBase
import org.littletonrobotics.junction.Logger as AkitLogger

object Logger {
    /**
     * Initializes and starts advantagekit with some default settings, records [projectName] as metadata.
     */
    fun initAdvantagekit(projectName: String) {
        AkitLogger.recordMetadata("ProjectName", projectName)
        AkitLogger.recordMetadata("BuildDate", BuildConstants.BUILD_DATE)
        AkitLogger.recordMetadata("GitSHA", BuildConstants.GIT_SHA)
        AkitLogger.recordMetadata("GitDate", BuildConstants.GIT_DATE)
        AkitLogger.recordMetadata("GitBranch", BuildConstants.GIT_BRANCH)
        when (BuildConstants.DIRTY) {
            0 -> AkitLogger.recordMetadata("GitDirty", "All changes committed")
            1 -> AkitLogger.recordMetadata("GitDirty", "Uncomitted changes")
            else -> AkitLogger.recordMetadata("GitDirty", "Unknown")
        }

        if (RobotBase.isReal || State.mode == State.Mode.SIM) {
            AkitLogger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
            AkitLogger.addDataReceiver(NT4Publisher()) // Publish data to NetworkTables
            PowerDistribution(1, PowerDistribution.ModuleType.kRev) // Enables power distribution logging
        } else if (State.mode == State.Mode.REPLAY) {
            Robot.useTiming = false // Run as fast as possible
            val logPath = LogFileUtil.findReplayLog() // Pull the replay log from AdvantageScope (or prompt the user)
            AkitLogger.setReplaySource(WPILOGReader(logPath)) // Read replay log
            AkitLogger.addDataReceiver(
                WPILOGWriter(
                    LogFileUtil.addPathSuffix(
                        logPath,
                        "_sim"
                    )
                )
            ) // Save outputs to a new log
        }

        // Logger.disableDeterministicTimestamps() // See "Deterministic Timestamps" in the "Understanding Data Flow" page
        AkitLogger.start()
    }
}