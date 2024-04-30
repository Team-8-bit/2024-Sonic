package org.team9432.lib.dashboard

import kotlinx.serialization.Serializable

@Serializable
data class ValueUpdateMessage(val key: String, val value: Type)