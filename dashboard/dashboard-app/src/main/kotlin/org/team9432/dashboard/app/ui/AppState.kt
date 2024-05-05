package org.team9432.dashboard.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Manages state of the app. */
object AppState {
    /** The screen that should be displayed. */
    var screen by mutableStateOf(Screen.DATA_VIEW)

    /** The index of the currently selected tab. */
    var currentTab by mutableIntStateOf(0)

    /** Whether the dashboard is connected to the robot. */
    var connected by mutableStateOf(false)

    /** Whether the app is in dark mode. */
    var isDarkMode by mutableStateOf(true)

    /** Options for screens to display. */
    enum class Screen {
        SETTINGS,
        DATA_VIEW
    }
}