package org.team9432.lib.dashboard

import kotlinx.serialization.Serializable

@Serializable
sealed interface Message

@Serializable
data class ValueUpdateMessage(val key: String, val value: Type): Message

@Serializable
data class InitialMessage(val messages: List<Message>): Message