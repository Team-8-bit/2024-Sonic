package org.team9432.dashboard.shared

import kotlinx.serialization.Serializable

/** An interface representing any piece of information being moved between the robot and the dashboard. */
@Serializable
sealed interface Sendable