package org.team9432.dashboard.app.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.team9432.dashboard.app.io.Client
import org.team9432.dashboard.app.io.Config
import org.team9432.dashboard.app.ui.AppState

@Composable
fun SettingsScreen() {
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(Modifier.fillMaxWidth(0.9F).fillMaxHeight()) {
                BooleanConfig("Dark Mode", AppState.isDarkMode) { Config.setDarkMode(it) }
                StringConfig("Robot IP", Config.getRobotIP()) { Config.setRobotIP(it) }
                StringConfig("Robot Port", Config.getRobotPort(), numberOnly = true) { Config.setRobotPort(it) }
                ButtonConfig("Reconnect") { Client.reconnect() }
            }
        }
    }
}

@Composable
private fun ButtonConfig(title: String, onClick: () -> Unit) {
    ConfigBase(title) { Button(onClick = onClick) { Text(title) } }
}

@Composable
private fun StringConfig(title: String, currentValue: String, numberOnly: Boolean = false, onChange: (String) -> Unit) {
    var currentString by remember { mutableStateOf(currentValue) }
    var isError by remember { mutableStateOf(false) }
    ConfigBase(title) {
        OutlinedTextField(
            currentString, { newValue ->
                if (numberOnly && !newValue.all { it.isDigit() }) {
                    isError = true
                } else {
                    isError = false
                    onChange(newValue)
                }
                currentString = newValue
            },
            singleLine = true,
            isError = isError
        )
    }
}

@Composable
private fun BooleanConfig(title: String, isActive: Boolean, onChange: (Boolean) -> Unit) {
    ConfigBase(title) { Switch(isActive, onChange) }
}

@Composable
private fun ConfigBase(title: String, content: @Composable () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = content
    )
}