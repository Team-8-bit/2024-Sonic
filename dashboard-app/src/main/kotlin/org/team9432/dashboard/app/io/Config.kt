package org.team9432.dashboard.app.io

import org.team9432.dashboard.app.ui.AppState
import java.util.prefs.Preferences

object Config {
    private const val DARK_MODE = "darkmode"
    private const val ROBOT_IP = "robotip"
    private const val ROBOT_PORT = "robotport"

    private val config = Preferences.userRoot().node("9432DashboardConfig")

    fun refreshAppStateFromConfig() {
        val darkMode = config.getBoolean(DARK_MODE, true)

        AppState.isDarkMode = darkMode
    }

    fun setDarkMode(enabled: Boolean) {
        AppState.isDarkMode = enabled
        config.putBoolean(DARK_MODE, enabled)
    }

    fun setRobotIP(ip: String) = config.put(ROBOT_IP, ip)
    fun getRobotIP() = config.get(ROBOT_IP, "0.0.0.0")
    fun setRobotPort(port: String) = config.put(ROBOT_PORT, port)
    fun getRobotPort() = config.get(ROBOT_PORT, "0000")
}